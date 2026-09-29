import { IAuthRepository } from '../../domain/repositories/IAuthRepository';
import { User } from '../../domain/entities/User';
import { createClient } from '../../utils/supabase/server';

export class SupabaseAuthRepository implements IAuthRepository {
  async signIn(email: string, password: string): Promise<User> {
    const supabase = await createClient();
    const { data, error } = await supabase.auth.signInWithPassword({
      email,
      password
    });
    
    if (error || !data.user) {
      throw new Error(error?.message || 'Falha na autenticação');
    }
    
    return {
      id: data.user.id,
      email: data.user.email!,
    };
  }

  async signUp(email: string, password: string, name?: string): Promise<User> {
    const supabase = await createClient();
    const { data, error } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: {
          name: name
        }
      }
    });

    if (error || !data.user) {
      throw new Error(error?.message || 'Falha ao criar conta');
    }

    return {
      id: data.user.id,
      email: data.user.email!,
      name: name
    };
  }

  async signOut(): Promise<void> {
    const supabase = await createClient();
    const { error } = await supabase.auth.signOut();
    if (error) {
      throw new Error(error.message);
    }
  }

  async getCurrentUser(): Promise<User | null> {
    const supabase = await createClient();
    const { data: { user }, error } = await supabase.auth.getUser();
    
    if (error || !user) {
      return null;
    }

    return {
      id: user.id,
      email: user.email!,
      name: user.user_metadata?.name
    };
  }
}
