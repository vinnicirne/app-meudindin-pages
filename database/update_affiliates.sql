-- Adiciona colunas para instagram e telefone na tabela de afiliados
ALTER TABLE public.affiliates
ADD COLUMN IF NOT EXISTS instagram text,
ADD COLUMN IF NOT EXISTS phone text;
