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

app.post("/create_preference", async (req, res) => {
  try {
    const preference = new Preference(client);

    const body = {
      items: [
        {
          id: "meu_dindin_vitalicio",
          title: "Meu DinDin - Acesso Vitalício",
          quantity: 1,
          unit_price: 29.00,
          currency_id: "BRL",
        }
      ]
    };

    const response = await preference.create({ body });
    // Retorna o ID da preferência e o link de pagamento do Checkout Pro
    res.json({ id: response.id, init_point: response.init_point });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to create preference" });
  }
});

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});
