'use server';

import { AddTransactionUseCase } from '../../application/usecases/AddTransactionUseCase';
import { SupabaseTransactionRepository } from '../../infrastructure/database/SupabaseTransactionRepository';
import { supabaseClient } from '../../infrastructure/database/supabaseClient';

const transactionRepository = new SupabaseTransactionRepository();
const addTransactionUseCase = new AddTransactionUseCase(transactionRepository);

export async function addTransactionAction(formData: FormData) {
  try {
    // Phase 2.5: Autenticação, Autorização e Segurança
    const { data: { user }, error: authError } = await supabaseClient.auth.getUser();
    
    if (authError || !user) {
      return { error: 'Não autorizado. Faça login para adicionar transações.' };
    }

    const userId = user.id;

    // Phase 2.4: Parse e validação de entrada
    const amount = Number(formData.get('amount'));
    const description = formData.get('description') as string;
    const dateStr = formData.get('date') as string;
    const type = formData.get('type') as 'INCOME' | 'EXPENSE';
    const categoryId = formData.get('categoryId') as string;
    const isRecurring = formData.get('isRecurring') === 'true';
    const installmentsTotal = Number(formData.get('installmentsTotal'));

    if (!amount || !description || !dateStr || !type || !categoryId) {
      return { error: 'Campos obrigatórios ausentes.' };
    }

    const transaction = await addTransactionUseCase.execute({
      userId,
      amount,
      description,
      date: new Date(dateStr),
      categoryId,
      type,
      isRecurring,
      installments: installmentsTotal > 1 ? { current: 1, total: installmentsTotal } : undefined
    });

    return { success: true, transactionId: transaction.id };
  } catch (error: any) {
    return { error: error.message };
  }
}
