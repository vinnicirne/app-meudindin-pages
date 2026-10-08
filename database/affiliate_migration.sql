-- Adiciona suporte a afiliados diretamente na tabela users
-- NAO REMOVE dados existentes - apenas adiciona colunas novas

ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS is_affiliate BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS affiliate_code TEXT UNIQUE;

-- Adiciona coluna referral tracking (quem indicou este usuario)
ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS referred_by TEXT;

-- Indice para busca por codigo de afiliado
CREATE INDEX IF NOT EXISTS idx_users_affiliate_code ON public.users(affiliate_code);
CREATE INDEX IF NOT EXISTS idx_users_referred_by ON public.users(referred_by);
