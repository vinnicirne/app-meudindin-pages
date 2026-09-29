-- ==============================================================================
-- SCHEMA DA TABELA DE TRANSAÇÕES (TRANSACTIONS) NO SUPABASE
-- Execute este script no SQL Editor do Supabase para criar/atualizar a tabela
-- ==============================================================================

-- 1. Cria a tabela transactions caso não exista
CREATE TABLE IF NOT EXISTS public.transactions (
  id uuid PRIMARY KEY DEFAULT gen_randomUUID(),
  user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
  amount numeric(12,2) NOT NULL,
  description text NOT NULL,
  date timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  category_id text NOT NULL,
  type text NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
  installments jsonb DEFAULT NULL,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL,
  updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Garante que as colunas existam caso a tabela já tenha sido criada anteriormente
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS notes text DEFAULT '';
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS is_paid boolean DEFAULT true;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS installments jsonb DEFAULT NULL;
ALTER TABLE public.transactions ADD COLUMN IF NOT EXISTS is_recurring boolean DEFAULT false;

-- 3. Habilita o RLS (Row Level Security)
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- 4. Cria políticas de segurança RLS para os usuários
DROP POLICY IF EXISTS "Users can view own transactions" ON public.transactions;
CREATE POLICY "Users can view own transactions" ON public.transactions
  FOR SELECT USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can insert own transactions" ON public.transactions;
CREATE POLICY "Users can insert own transactions" ON public.transactions
  FOR INSERT WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can update own transactions" ON public.transactions;
CREATE POLICY "Users can update own transactions" ON public.transactions
  FOR UPDATE USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can delete own transactions" ON public.transactions;
CREATE POLICY "Users can delete own transactions" ON public.transactions
  FOR DELETE USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Service Role has full access to transactions" ON public.transactions;
CREATE POLICY "Service Role has full access to transactions" ON public.transactions
  FOR ALL USING (auth.role() = 'service_role');

-- 5. Recarrega o cache do PostgREST schema
NOTIFY pgrst, 'reload schema';
