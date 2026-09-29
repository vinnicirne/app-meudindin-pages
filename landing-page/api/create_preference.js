import { MercadoPagoConfig, Preference } from "mercadopago";

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method Not Allowed' });
  }

  try {
    const client = new MercadoPagoConfig({ 
      accessToken: process.env.MP_ACCESS_TOKEN,
      options: { timeout: 5000 } 
    });

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
    // Retorna o ID da preferência e o link de pagamento do Checkout Pro
    res.status(200).json({ id: response.id, init_point: response.init_point });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Failed to create preference" });
  }
}
