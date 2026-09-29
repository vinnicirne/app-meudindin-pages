import { Transaction, TransactionProps } from '../../domain/entities/Transaction';
import { ITransactionRepository } from '../../domain/repositories/ITransactionRepository';

export interface AddTransactionRequestDTO {
  userId: string;
  amount: number;
  description: string;
  date: Date;
  categoryId: string;
  type: 'INCOME' | 'EXPENSE';
  isRecurring?: boolean;
  installments?: {
    current: number;
    total: number;
  };
}

export class AddTransactionUseCase {
  constructor(private transactionRepository: ITransactionRepository) {}

  async execute(request: AddTransactionRequestDTO): Promise<Transaction> {
    const transaction = new Transaction(request);

    // Na arquitetura Unicórnio, nós delegamos a persistência
    await this.transactionRepository.create(transaction);

    return transaction;
  }
}
