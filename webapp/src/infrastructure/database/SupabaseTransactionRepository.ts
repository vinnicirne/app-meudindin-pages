import { Transaction } from '../../domain/entities/Transaction';
import { ITransactionRepository } from '../../domain/repositories/ITransactionRepository';
import { createClient } from '../../utils/supabase/server';

export class SupabaseTransactionRepository implements ITransactionRepository {
  private tableName = 'transactions';

  async create(transaction: Transaction): Promise<void> {
    const data = transaction.toJSON();
    const supabase = await createClient();
    
    // Tratando o mapeamento para o banco se necessário, por exemplo, extraindo installments para jsonb.
    const { error } = await supabase
      .from(this.tableName)
      .insert([
        {
          id: data.id,
          user_id: data.userId,
          amount: data.amount,
          description: data.description,
          date: data.date.toISOString(),
          category_id: data.categoryId,
          type: data.type,
          installments: data.installments,
          created_at: data.createdAt,
          updated_at: data.updatedAt,
        }
      ]);

    if (error) {
      throw new Error(`Erro ao criar transação: ${error.message}`);
    }
  }

  async findById(id: string): Promise<Transaction | null> {
    const supabase = await createClient();
    const { data, error } = await supabase
      .from(this.tableName)
      .select('*')
      .eq('id', id)
      .single();

    if (error || !data) return null;

    return new Transaction({
      id: data.id,
      userId: data.user_id,
      amount: data.amount,
      description: data.description,
      date: new Date(data.date),
      categoryId: data.category_id,
      type: data.type,
      installments: data.installments,
      createdAt: new Date(data.created_at),
      updatedAt: new Date(data.updated_at)
    });
  }

  async findByUserId(userId: string, filters?: { startDate?: Date; endDate?: Date; type?: 'INCOME' | 'EXPENSE' }): Promise<Transaction[]> {
    const supabase = await createClient();
    let query = supabase.from(this.tableName).select('*').eq('user_id', userId);

    if (filters?.startDate) query = query.gte('date', filters.startDate.toISOString());
    if (filters?.endDate) query = query.lte('date', filters.endDate.toISOString());
    if (filters?.type) query = query.eq('type', filters.type);

    const { data, error } = await query;

    if (error) {
      throw new Error(`Erro ao buscar transações: ${error.message}`);
    }

    return (data || []).map(row => new Transaction({
      id: row.id,
      userId: row.user_id,
      amount: row.amount,
      description: row.description,
      date: new Date(row.date),
      categoryId: row.category_id,
      type: row.type,
      installments: row.installments,
      createdAt: new Date(row.created_at),
      updatedAt: new Date(row.updated_at)
    }));
  }

  async update(transaction: Transaction): Promise<void> {
    const data = transaction.toJSON();
    const supabase = await createClient();
    
    const { error } = await supabase
      .from(this.tableName)
      .update({
        amount: data.amount,
        description: data.description,
        date: data.date.toISOString(),
        category_id: data.categoryId,
        type: data.type,
        installments: data.installments,
        updated_at: new Date().toISOString(),
      })
      .eq('id', data.id);

    if (error) {
      throw new Error(`Erro ao atualizar transação: ${error.message}`);
    }
  }

  async delete(id: string): Promise<void> {
    const supabase = await createClient();
    const { error } = await supabase
      .from(this.tableName)
      .delete()
      .eq('id', id);

    if (error) {
      throw new Error(`Erro ao excluir transação: ${error.message}`);
    }
  }
}
