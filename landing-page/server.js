import express from "express";
import cors from "cors";
import { MercadoPagoConfig, Preference } from "mercadopago";
import dotenv from "dotenv";

dotenv.config();

const app = express();
const port = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
// Serve os arquivos estáticos (HTML, CSS, JS) da mesma pasta
app.use(express.static('./'));

// 1. Configure com seu ACCESS_TOKEN do Mercado Pago
// Você pode colocar a chave no arquivo .env
const client = new MercadoPagoConfig({ 
  accessToken: process.env.MP_ACCESS_TOKEN || 'SEU_ACCESS_TOKEN_AQUI',
  options: { timeout: 5000 } 
});

import { createClient } from "@supabase/supabase-js";

// Inicializa o Supabase (requer variáveis no .env)
const supabaseUrl = process.env.SUPABASE_URL || 'https://sua-url-do-supabase.supabase.co';
const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY || 'sua-service-role-key';
const supabase = createClient(supabaseUrl, supabaseKey);

app.post("/api/register", async (req, res) => {
  try {
    const { email, password, name } = req.body;

    // 1. Cria usuário no Supabase
    const { data: authData, error: authError } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: { name },
      },
    });

    if (authError) throw authError;

    const userId = authData.user.id;

    // 2. Cria a preferência de pagamento amarrada ao ID do usuário
    const preference = new Preference(client);
    const body = {
      items: [
        {
          id: "meu_dindin_anual",
          title: "Meu DinDin - Assinatura Anual",
          quantity: 1,
          unit_price: 1.00,
          currency_id: "BRL",
        }
      ],
      external_reference: String(userId),
      back_urls: {
        success: "https://localhost:3000/login",
        failure: "https://localhost:4000/cadastro.html",
        pending: "https://localhost:4000/cadastro.html",
      },
      auto_return: "approved",
    };

    const response = await preference.create({ body });
    res.json({ id: response.id, init_point: response.init_point });
  } catch (error) {
    console.error("Register Error:", error);
    res.status(500).json({ error: "Failed to register user and create preference" });
  }
});

// Webhook para escutar o Mercado Pago
app.post("/api/webhooks/mercadopago", async (req, res) => {
  // O MP exige resposta 200 rápida
  res.status(200).send("OK");

  try {
    const topic = req.body.type || req.query.topic;
    const paymentId = req.body.data?.id || req.query.id;

    if (topic === "payment" && paymentId) {
      // Usando fetch nativo ou o SDK para buscar o pagamento
      const paymentResponse = await fetch(`https://api.mercadopago.com/v1/payments/${paymentId}`, {
        headers: { Authorization: `Bearer ${process.env.MP_ACCESS_TOKEN}` }
      });
      const payment = await paymentResponse.json();

      if (payment.status === "approved") {
        const userId = payment.external_reference;
        
        if (userId) {
          // Atualiza status do usuário no banco para ativo
          // Como não temos a estrutura da tabela, assumimos uma chamada RPC ou update
          // await supabase.from('users').update({ plan_status: 'active' }).eq('id', userId);
          console.log(`✅ Pagamento aprovado! Usuário liberado: ${userId}`);
        }
      }
    }
  } catch (err) {
    console.error("Webhook Error:", err);
  }
});

// Rota legada para compatibilidade
app.post("/api/create_preference", async (req, res) => {
  try {
    const preference = new Preference(client);
    const body = {
      items: [
        {
          id: "meu_dindin_anual",
          title: "Meu DinDin - Assinatura Anual",
          quantity: 1,
          unit_price: 1.00,
          currency_id: "BRL",
        }
      ],
      back_urls: {
        success: "https://localhost:3000/login",
        failure: "https://localhost:4000/cadastro.html",
        pending: "https://localhost:4000/cadastro.html",
      },
      auto_return: "approved",
    };

    const response = await preference.create({ body });
    res.json({ id: response.id, init_point: response.init_point });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to create preference" });
  }
});

app.listen(port, () => {
  console.log(`Servidor rodando em https://localhost:${port}`);
});
