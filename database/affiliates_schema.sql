-- ==============================================================================
-- SCHEMA DO SISTEMA DE AFILIADOS / INFLUENCERS
-- Execute este script no SQL Editor do seu Supabase Dashboard
-- ==============================================================================

-- 1. Cria a tabela de afiliados
CREATE TABLE IF NOT EXISTS public.affiliates (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text NOT NULL,
  code text NOT NULL UNIQUE,
  commission_type text NOT NULL DEFAULT 'fixed', -- 'fixed' ou 'percentage'
  commission_value numeric(10,2) NOT NULL DEFAULT 0,
  pix_key text DEFAULT '',
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Habilita RLS na tabela afiliados
ALTER TABLE public.affiliates ENABLE ROW LEVEL SECURITY;

-- Apenas Admin / Service Role podem ver ou editar os afiliados
CREATE POLICY "Service Role has full access to affiliates" ON public.affiliates
  FOR ALL USING (auth.role() = 'service_role');

-- 3. Adiciona a coluna referred_by na tabela users (se não existir)
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS referred_by text;

-- 4. Atualiza a função de novo usuário para gravar também o referred_by
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS 
BEGIN
  INSERT INTO public.users (id, name, email, phone, plan_status, referred_by)
  VALUES (
    NEW.id,
    NEW.raw_user_meta_data->>'name',
    NEW.email,
    NEW.raw_user_meta_data->>'phone',
    'pending',
    NEW.raw_user_meta_data->>'referred_by'
  )
  ON CONFLICT (id) DO UPDATE
  SET 
    name = EXCLUDED.name,
    email = EXCLUDED.email,
    phone = EXCLUDED.phone,
    referred_by = COALESCE(EXCLUDED.referred_by, public.users.referred_by);
  RETURN NEW;
END;
 LANGUAGE plpgsql SECURITY DEFINER;

-- 5. Recarrega o cache do PostgREST schema para a API reconhecer as novas colunas
NOTIFY pgrst, 'reload schema';
