// src/hooks/useUsers.ts (Updated: Add searchUsers method)
import { useState, useEffect } from 'react';
import { getUsers, createUser, updateUser, deleteUser } from '../api';
import type { User, SearchParams } from '../types';
import { differenceInCalendarYears, addDays } from 'date-fns';

export function getYears(d: Date): number {
  const start = d;
  const end = addDays(new Date(), 1);  // Tomorrow, local time (system default)
  return differenceInCalendarYears(start, end);
}

export const useUsers = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    searchUsers({ criteria: 'TODOS' });  // Initial load
    window.addEventListener('online', () => searchUsers({ criteria: 'TODOS' }));
    return () => window.removeEventListener('online', () => searchUsers({ criteria: 'TODOS' }));
  }, []);

  const searchUsers = async (params: SearchParams) => {
    setLoading(true);
    setError(null);
    const data = await getUsers(params);
	console.log('API Response:', data);  // Debug: Log raw data
	console.log('Is Array?', Array.isArray(data), 'Length:', data.length);
    setUsers(data);
    setLoading(false);
  };

  const addUser = async (user: User) : Promise<{ success: boolean; data?: User; message: string } | null> => {
    setError(null);
    const result = await createUser(user);
    if (result.success) {
	  user.fechaNacimiento = user.fechaNacimiento + " (" + getYears(new Date(user.fechaNacimiento)) + " años)";
      setUsers(prev => [...prev, user]);
    } else {
      setError(result.error || 'Creación fallida de usuario');
    }
	return result;
  };

  const editUser = async (id: string, user: Omit<User, "rut">): Promise<{ success: boolean; data?: User; message: string } | null> => {
  setError(null);
  setMessage(null);
  if (id === undefined) id = user.rut as string;  // Fallback to user.rut (type cast if needed)
  try {
    const result = await updateUser(id, user);
    if (result && result.success) {
	  user.fechaNacimiento = user.fechaNacimiento + " (" + getYears(new Date(user.fechaNacimiento)) + " años)";
      setUsers(prev => prev.map(u => u.id === id ? user : u));  // Use result.data for update
      setMessage(result.message || 'Usuario actualizado exitosamente');
      return result;
    } else if (result && !result.success) {
      setError(result.error || 'Actualización fallida');
      setMessage(result.message || 'No se pudo actualizar el usuario');
      return result;
    } else {
      // Undefined result (network/rejection)
      const errMsg = 'Error de red durante actualización';
      setError(errMsg);
      setMessage(errMsg);
      return { success: false, message: errMsg };
    }
  } catch (err) {
    const errMsg = 'Error inesperado en actualización: ' + err;
    setError(errMsg);
    setMessage(errMsg);
    console.error('editUser error:', err);
    return { success: false, message: errMsg };
  }
};

  const removeUser = async (id: string) => {
    setError(null);
	const result = await deleteUser(id);
    if (result.success) {
      setUsers(prev => prev.filter(u => u.rut !== id));
    } else {
      setError(result.error || 'Eliminación fallida de usuario');
    }
  };

  return { users, loading, error, searchUsers, addUser, editUser, removeUser, setError };
};