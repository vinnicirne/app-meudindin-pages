CREATE TABLE IF NOT EXISTS public.categories (
    id TEXT PRIMARY KEY,
    label TEXT NOT NULL,
    icon TEXT NOT NULL,
    color TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO public.categories (id, label, icon, color) VALUES 
('alimentacao', 'Alimentação', 'restaurant', 'amber-500'),
('transporte', 'Transporte', 'directions_car', 'blue-500'),
('moradia', 'Moradia', 'home', 'indigo-500'),
('salario', 'Salário Mensal', 'attach_money', '#1db576'),
('lazer', 'Lazer & Entretenimento', 'sports_esports', 'purple-500'),
('saude', 'Saúde & Farmácia', 'medical_services', 'rose-500'),
('educacao', 'Educação', 'school', 'indigo-500'),
('servicos', 'Serviços', 'receipt_long', 'teal-500'),
('investimentos', 'Investimentos', 'trending_up', 'emerald-500'),
('outros', 'Outros', 'more_horiz', 'gray-500')
ON CONFLICT (id) DO NOTHING;

ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public read access to active categories" ON public.categories FOR SELECT USING (true);
CREATE POLICY "Admin write access to categories" ON public.categories FOR ALL USING (
    EXISTS (SELECT 1 FROM users WHERE users.id = auth.uid() AND users.role = 'admin')
);
