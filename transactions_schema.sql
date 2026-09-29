-- ==============================================================================
-- SCHEMA DEFINITIVO DA TABELA TRANSACTIONS (COMPATÍVEL WEB E MOBILE)
-- Execute este script no SQL Editor do seu Supabase Dashboard
-- ==============================================================================

-- 1. Garante a tabela base com compatibilidade total
CREATE TABLE IF NOT EXISTS public.transactions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE,
  amount numeric(12,2) NOT NULL DEFAULT 0,
  description text NOT NULL DEFAULT '',
  date timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  category_id text NOT NULL DEFAULT 'outros',
  type text NOT NULL DEFAULT 'EXPENSE',
  notes text DEFAULT '',
  is_paid boolean DEFAULT true,
  is_recurring boolean DEFAULT false,
  installments jsonb DEFAULT NULL,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
  updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Adiciona todas as colunas necessárias caso a tabela já existisse no seu Supabase
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS amount numeric(12,2) NOT NULL DEFAULT 0;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS description text NOT NULL DEFAULT '';
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS title text;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS date timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now());
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS category_id text NOT NULL DEFAULT 'outros';
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS type text NOT NULL DEFAULT 'EXPENSE';
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS notes text DEFAULT '';
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS is_paid boolean DEFAULT true;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS is_recurring boolean DEFAULT false;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS installments jsonb DEFAULT NULL;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL;

-- 3. Habilita o RLS (Row Level Security)
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- 4. Cria políticas de segurança
DROP POLICY IF EXISTS "Users can view own transactions" ON public.transactions;
CREATE POLICY "Users can view own transactions" ON public.transactions
  FOR SELECT USING (auth.uid() = user_id OR auth.uid() IS NULL);

DROP POLICY IF EXISTS "Users can insert own transactions" ON public.transactions;
CREATE POLICY "Users can insert own transactions" ON public.transactions
  FOR INSERT WITH CHECK (auth.uid() = user_id OR auth.uid() IS NULL);

DROP POLICY IF EXISTS "Users can update own transactions" ON public.transactions;
CREATE POLICY "Users can update own transactions" ON public.transactions
  FOR UPDATE USING (auth.uid() = user_id OR auth.uid() IS NULL);

DROP POLICY IF EXISTS "Users can delete own transactions" ON public.transactions;
CREATE POLICY "Users can delete own transactions" ON public.transactions
  FOR DELETE USING (auth.uid() = user_id OR auth.uid() IS NULL);

DROP POLICY IF EXISTS "Service Role has full access to transactions" ON public.transactions;
CREATE POLICY "Service Role has full access to transactions" ON public.transactions
  FOR ALL USING (auth.role() = 'service_role');

-- 5. Recarrega o cache do PostgREST schema para a API reconhecer as novas colunas
NOTIFY pgrst, 'reload schema';
