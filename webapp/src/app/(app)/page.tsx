import { redirect } from 'next/navigation'
import { createClient } from '@/utils/supabase/server'
import HomeClient from './HomeClient'

export default async function Home() {
  const supabase = await createClient()
  const { data: { user } } = await supabase.auth.getUser()

  if (!user) redirect('/login')

  // Busca todas as transações do usuário
  const { data: transactions } = await supabase
    .from('transactions')
    .select('*')
    .eq('user_id', user.id)
    .order('date', { ascending: false })

  return <HomeClient transactions={transactions || []} />
}
