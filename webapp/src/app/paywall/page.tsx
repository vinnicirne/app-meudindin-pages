import { redirect } from 'next/navigation'
import { createClient } from '@/utils/supabase/server'
import PaywallClient from './PaywallClient'

/**
 * Paywall — Server Component
 * Busca os dados do usuário logado e passa para o client component
 * que irá gerar o link de pagamento personalizado.
 */
export default async function PaywallPage() {
  const supabase = await createClient()
  const { data: { user } } = await supabase.auth.getUser()

  // Se não estiver logado, manda para o cadastro (início do funil)
  if (!user) {
    redirect('/cadastro')
  }

  // Se já está ativo, não precisa do paywall
  const { data: userData } = await supabase
    .from('users')
    .select('plan_status, name, email')
    .eq('id', user.id)
    .single()

  if (userData?.plan_status === 'active') {
    redirect('/')
  }

  return (
    <PaywallClient
      userId={user.id}
      userEmail={userData?.email || user.email || ''}
      userName={userData?.name || ''}
    />
  )
}
