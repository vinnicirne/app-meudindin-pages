-- Tabela de Planos do Meu DinDin
CREATE TABLE IF NOT EXISTS public.plans (
  id text PRIMARY KEY,
  name text NOT NULL,
  description text,
  price numeric(10,2) NOT NULL,
  interval text NOT NULL DEFAULT 'year', -- 'year', 'month'
  features jsonb DEFAULT '[]'::jsonb,
  is_active boolean DEFAULT true,
  badge text,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Habilita RLS
ALTER TABLE public.plans ENABLE ROW LEVEL SECURITY;

-- Usuários autenticados podem ver os planos ativos
CREATE POLICY "Anyone can view active plans" ON public.plans
  FOR SELECT USING (true);

-- Apenas admins podem criar/editar/excluir planos
CREATE POLICY "Admins can manage plans" ON public.plans
  FOR ALL USING (
    EXISTS (
      SELECT 1 FROM public.users
      WHERE users.id = auth.uid() AND users.role = 'admin'
    )
  );

-- Insere o plano padrão anual se não existir
INSERT INTO public.plans (id, name, description, price, interval, features, is_active, badge, sort_order)
VALUES (
  'meu_dindin_anual',
  'Plano Anual Oficial',
  'Acesso completo ao Meu DinDin por 12 meses',
  29.00,
  'year',
  '["Controle financeiro completo", "Relatórios e gráficos", "Categorias ilimitadas", "Exportação de dados", "Suporte prioritário"]'::jsonb,
  true,
  'MAIS POPULAR',
  1
) ON CONFLICT (id) DO NOTHING;
