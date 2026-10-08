-- ==========================================
-- 1. Tabela: vaults (As Caixinhas)
-- ==========================================
CREATE TABLE IF NOT EXISTS public.vaults (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  goal_amount NUMERIC(15, 2), -- Opcional: Se a pessoa quiser definir "Quero chegar a R$ 10k"
  icon TEXT DEFAULT 'savings',
  color TEXT DEFAULT 'bg-blue-500',
  created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

-- Habilita RLS na tabela vaults
ALTER TABLE public.vaults ENABLE ROW LEVEL SECURITY;

-- Políticas de segurança (RLS) para vaults
DROP POLICY IF EXISTS "Usuários podem ver suas próprias caixinhas" ON public.vaults;
CREATE POLICY "Usuários podem ver suas próprias caixinhas" 
  ON public.vaults FOR SELECT 
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Usuários podem criar suas próprias caixinhas" ON public.vaults;
CREATE POLICY "Usuários podem criar suas próprias caixinhas" 
  ON public.vaults FOR INSERT 
  WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Usuários podem atualizar suas próprias caixinhas" ON public.vaults;
CREATE POLICY "Usuários podem atualizar suas próprias caixinhas" 
  ON public.vaults FOR UPDATE 
  USING (auth.uid() = user_id) 
  WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Usuários podem deletar suas próprias caixinhas" ON public.vaults;
CREATE POLICY "Usuários podem deletar suas próprias caixinhas" 
  ON public.vaults FOR DELETE 
  USING (auth.uid() = user_id);

-- ==========================================
-- 2. Tabela: vault_transactions (Movimentações)
-- ==========================================
CREATE TABLE IF NOT EXISTS public.vault_transactions (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  vault_id UUID NOT NULL REFERENCES public.vaults(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  type TEXT NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'YIELD')), 
  amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
  date TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc', now()) NOT NULL
);

-- Habilita RLS na tabela vault_transactions
ALTER TABLE public.vault_transactions ENABLE ROW LEVEL SECURITY;

-- Políticas de segurança (RLS) para vault_transactions
DROP POLICY IF EXISTS "Usuários podem ver movimentações de suas caixinhas" ON public.vault_transactions;
CREATE POLICY "Usuários podem ver movimentações de suas caixinhas" 
  ON public.vault_transactions FOR SELECT 
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Usuários podem inserir movimentações" ON public.vault_transactions;
CREATE POLICY "Usuários podem inserir movimentações" 
  ON public.vault_transactions FOR INSERT 
  WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Usuários podem deletar movimentações" ON public.vault_transactions;
CREATE POLICY "Usuários podem deletar movimentações" 
  ON public.vault_transactions FOR DELETE 
  USING (auth.uid() = user_id);

-- Trigger para atualizar o updated_at da caixinha quando houver movimentação
CREATE OR REPLACE FUNCTION update_vault_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE public.vaults SET updated_at = timezone('utc', now()) WHERE id = NEW.vault_id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_vault_updated_at ON public.vault_transactions;
CREATE TRIGGER trigger_update_vault_updated_at
AFTER INSERT OR UPDATE OR DELETE ON public.vault_transactions
FOR EACH ROW EXECUTE FUNCTION update_vault_updated_at();

-- Trigger padrão para updated_at no UPDATE direto do vault
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = timezone('utc', now());
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_updated_at_vaults ON public.vaults;
CREATE TRIGGER set_updated_at_vaults
BEFORE UPDATE ON public.vaults
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
