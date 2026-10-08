-- Migration: Adicionar colunas de assinatura à tabela users
-- Execute este script no SQL Editor do Supabase

-- Adiciona colunas de controle de assinatura (se não existirem)
ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS mp_payment_id text,
  ADD COLUMN IF NOT EXISTS activated_at timestamp with time zone,
  ADD COLUMN IF NOT EXISTS expires_at timestamp with time zone,
  ADD COLUMN IF NOT EXISTS trial_ends_at timestamp with time zone,
  ADD COLUMN IF NOT EXISTS role text DEFAULT 'user';

-- Atualiza o enum de plan_status para incluir todos os estados usados no sistema
-- (Comentado pois o campo é text — sem necessidade de enum)

-- Cria índice para busca rápida por plan_status (ex: relatórios do admin)
CREATE INDEX IF NOT EXISTS idx_users_plan_status ON public.users(plan_status);

-- Cria índice para busca por mp_payment_id (ex: idempotência no webhook)
CREATE INDEX IF NOT EXISTS idx_users_mp_payment_id ON public.users(mp_payment_id);
