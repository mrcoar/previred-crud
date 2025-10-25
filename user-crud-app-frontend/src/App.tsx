// src/App.tsx (Updated: Integrate SearchForm)
import React, { useState, useEffect } from 'react';
import { useUsers } from './hooks/useUsers';
import { useGeoData } from './hooks/useGeoData';
import { UserForm } from './components/UserForm';
import { UserList } from './components/UserList';
import { SearchForm } from './components/SearchForm';  // New import
import type { User } from './types';
import './App.css';

function App() {
  const { users, loading, error, searchUsers, addUser, editUser, removeUser, setError } = useUsers();
  const { regions, regionToComunas, loading: geoLoading } = useGeoData();
  const [editingUser, setEditingUser] = useState<User | null>(null);

  // Auto-clear error after 5s
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => setError(null), 5000);
      return () => clearTimeout(timer);
    }
  }, [error, setError]);

  const handleSubmit = async (data: User) => {
    if (editingUser) {
      const result = await editUser(editingUser.id, data);
	  if(!result.success) setEditingUser(null);
	  return result;
    } else {
	  const result = await addUser(data);
	  if(!result.success) setEditingUser(null);
	  return result;
    }
    
  };

  const handleEdit = (user: User) => setEditingUser(user);
  const handleDelete = (id: string) => {
    if (window.confirm('¿Desea eliminar al usuario con rut ' + id + '?')) removeUser(id);
	setEditingUser(null);
  };

  if (geoLoading) return <div>Loading geo data...</div>;

  return (
    <div className="App">
      <h1>Mantenedor de Usuarios</h1>
      <p>Estado Navegador: {navigator.onLine ? 'En línea' : 'Fuera de línea'}</p>
      {error && (
        <div className="error-banner" style={{ color: 'red', padding: '10px', border: '1px solid red', margin: '10px 0' }}>
          Error: {error}
        </div>
      )}
      <SearchForm
        regions={regions}
        getComunasForRegion={(id) => regionToComunas.get(id) || []}
        onSearch={searchUsers}
      />
      <UserForm
        user={editingUser}
        onSubmit={handleSubmit}
        onCancel={() => setEditingUser(null)}
        regions={regions}
        getComunasForRegion={(id) => regionToComunas.get(id) || []}
      />
      <UserList
        users={users}
        onEdit={handleEdit}
        onDelete={handleDelete}
        loading={loading}
        regions={regions}
        regionToComunas={regionToComunas}
      />
    </div>
  );
}

export default App;