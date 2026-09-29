'use server'

import { createClient } from '@/utils/supabase/server'
import { revalidatePath } from 'next/cache'
import { SubscriptionItem } from '@/types/subscription'

async function checkAdmin() {
  const supabase = await createClient()
  const { data: { user } } = await supabase.auth.getUser()
  if (!user) throw new Error('Não autenticado.')

  const { data: userData } = await supabase
    .from('users')
    .select('role')
    .eq('id', user.id)
    .single()

  if (userData?.role !== 'admin') throw new Error('Acesso não autorizado.')
  return supabase
}

export async function getSubscriptionsAction(): Promise<SubscriptionItem[]> {
  try {
    const supabase = await createClient()

    // 1. Busca os usuários
    const { data: usersData, error: usersError } = await supabase
      .from('users')
      .select('id, name, email, plan_status, created_at')
      .order('created_at', { ascending: false })

    if (usersError || !usersData) {
      return []
    }

    // 2. Mapeia os usuários em assinaturas
    const subscriptions: SubscriptionItem[] = usersData.map(u => {
      const isApproved = u.plan_status === 'active'
      const status: SubscriptionItem['status'] = isApproved
        ? 'active'
        : u.plan_status === 'blocked'
        ? 'canceled'
        : 'pending'

      const createdDate = new Date(u.created_at || Date.now())
      const expiresDate = new Date(createdDate)
      expiresDate.setFullYear(expiresDate.getFullYear() + 1)

      return {
        id: `sub_${u.id.slice(0, 8)}`,
        userId: u.id,
        userName: u.name || 'Sem nome',
        userEmail: u.email || '—',
        planId: 'meu_dindin_anual',
        planName: 'Plano Anual Oficial',
        amount: 29.00,
        status,
        interval: 'year',
        createdAt: u.created_at || new Date().toISOString(),
        expiresAt: isApproved ? expiresDate.toISOString() : null,
      }
    })

    return subscriptions
  } catch {
    return []
  }
}

export async function updateSubscriptionStatusAction(userId: string, newStatus: 'active' | 'pending' | 'canceled') {
  try {
    const supabase = await checkAdmin()

    const dbPlanStatus = newStatus === 'active' ? 'active' : newStatus === 'canceled' ? 'blocked' : 'pending'

    const { error } = await supabase
      .from('users')
      .update({ plan_status: dbPlanStatus })
      .eq('id', userId)

    if (error) throw error

    revalidatePath('/admin/subscriptions')
    revalidatePath('/admin/users')
    revalidatePath('/admin')
    return { success: true }
  } catch (err: any) {
    return { error: err.message || 'Erro ao atualizar assinatura.' }
  }
}
