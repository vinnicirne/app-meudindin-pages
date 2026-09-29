import { createClient } from "@supabase/supabase-js";

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method Not Allowed' });
  }

  // O MP exige resposta 200 rápida
  res.status(200).send("OK");

  try {
    const topic = req.body.type || req.query.topic;
    const paymentId = req.body.data?.id || req.query.id;

    if (topic === "payment" && paymentId) {
      // Usando fetch nativo para buscar o pagamento
      const paymentResponse = await fetch(`https://api.mercadopago.com/v1/payments/${paymentId}`, {
        headers: { Authorization: `Bearer ${process.env.MP_ACCESS_TOKEN}` }
      });
      const payment = await paymentResponse.json();

      if (payment.status === "approved") {
        const userId = payment.external_reference;
        
        if (userId) {
          const supabaseUrl = process.env.SUPABASE_URL || 'https://sua-url-do-supabase.supabase.co';
          const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY || 'sua-service-role-key';
          const supabase = createClient(supabaseUrl, supabaseKey);

          // Atualiza status do usuário no banco para ativo
          await supabase.from('users').update({ plan_status: 'active' }).eq('id', userId);
          console.log(`✅ Pagamento aprovado! Usuário liberado: ${userId}`);
        }
      }
    }
  } catch (err) {
    console.error("Webhook Error:", err);
  }
}
