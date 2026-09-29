import { MercadoPagoConfig, Preference } from "mercadopago";
import { createClient } from "@supabase/supabase-js";

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method Not Allowed' });
  }

  try {
    const { email, password, name } = req.body;

    const supabaseUrl = process.env.SUPABASE_URL || 'https://sua-url-do-supabase.supabase.co';
    const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY || 'sua-service-role-key';
    const supabase = createClient(supabaseUrl, supabaseKey);

    // 1. Cria usuário no Supabase
    const { data: authData, error: authError } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: { name },
      },
    });

    if (authError) throw authError;

    const userId = authData.user?.id || 'unknown';

    // 2. Cria a preferência de pagamento amarrada ao ID do usuário
    const client = new MercadoPagoConfig({ 
      accessToken: process.env.MP_ACCESS_TOKEN || 'SEU_ACCESS_TOKEN_AQUI',
      options: { timeout: 5000 } 
    });
    
    const preference = new Preference(client);
    
    // Altere estas URLs para a URL oficial em produção!
    const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://meudindin26.vercel.app';
    
    const body = {
      items: [
        {
          id: "meu_dindin_anual",
          title: "Meu DinDin - Assinatura Anual",
          quantity: 1,
          unit_price: 29.00,
          currency_id: "BRL",
        }
      ],
      external_reference: String(userId),
      back_urls: {
        success: `https://meudindin26.vercel.app/login`,
        failure: `https://meudindin26.vercel.app/cadastro.html`,
        pending: `https://meudindin26.vercel.app/cadastro.html`,
      },
      auto_return: "approved",
    };

    const response = await preference.create({ body });
    res.status(200).json({ id: response.id, init_point: response.init_point });
  } catch (error) {
    console.error("Register Error:", error);
    res.status(500).json({ 
      error: "Failed to register user and create preference", 
      details: error.message || String(error)
    });
  }
}
