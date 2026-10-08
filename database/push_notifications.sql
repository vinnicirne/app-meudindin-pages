-- Adicionar colunas para Push Notifications
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS fcm_token text;
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS push_enabled boolean DEFAULT true;
