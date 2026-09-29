'use server';

import { SupabaseAuthRepository } from '../../infrastructure/database/SupabaseAuthRepository';
import { AuthenticateUserUseCase } from '../../application/usecases/AuthenticateUserUseCase';

const authRepository = new SupabaseAuthRepository();
const authenticateUseCase = new AuthenticateUserUseCase(authRepository);

export async function loginAction(formData: FormData) {
  try {
    const email = formData.get('email') as string;
    const password = formData.get('password') as string;

    const user = await authenticateUseCase.execute(email, password);

    return { success: true, user };
  } catch (error: any) {
    return { error: error.message || 'Erro inesperado ao fazer login.' };
  }
}

export async function logoutAction() {
  try {
    await authRepository.signOut();
    return { success: true };
  } catch (error: any) {
    return { error: error.message };
  }
}
