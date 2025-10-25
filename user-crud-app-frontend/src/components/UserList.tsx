import React from 'react';
import type { User, Region } from '../types';

interface UserListProps {
  users: User[];
  onEdit: (user: User) => void;
  onDelete: (id: number) => void;
  loading: boolean;
  regions: Region[];
  regionToComunas: Map<string, { id: number; nombre: string }[]>;
}

export const UserList: React.FC<UserListProps> = ({ users, onEdit, onDelete, loading, regions, regionToComunas }) => {
  if (loading) return <div>Loading users...</div>;

  const getRegionName = (regionId?: string) => regions.find(r => r.id === regionId)?.nombre || regionId || 'N/A';
  const getComunaName = (regionId?: string, comunaId?: number) => {
    if (!regionId || !comunaId) return 'N/A';
    const comms = regionToComunas.get(regionId);
    return comms?.find(c => c.id === comunaId)?.nombre || comunaId.toString();
  };

  if (!Array.isArray(users)) {
    // Else: Handle non-array (text/error from API)
    return (
      <div className="user-list">
        <h2>Users</h2>
        <div className="error-message" style={{ color: 'red', padding: '10px', border: '1px solid red' }}>
          Error cargando usuarios: {typeof users === 'string' ? users : 'Invalid response format'}
        </div>
      </div>
    );
  }  
  if (users.length === 0) {
    return (
      <div className="user-list">
        <h2>Users</h2>
        <p>No users found.</p>
      </div>
    );
  }
  
  return (
    <div className="user-list">
      <h2>Lista de usuarios</h2>
      <ul>
        {users.map((user, index) => (  // Add index fallback
          <li key={user.rut || index} className="user-item">
            <div><strong>{user.nombre} {user.apellido}</strong> - Fecha de Nacimiento: {user.fechaNacimiento}</div>
            <div>Dirección: {user.calle}</div>
            <div>Región: {getRegionName(user.region)} | Comuna: {getComunaName(user.region, user.comuna)}</div>
            <button onClick={() => onEdit(user)}>Edit</button>
            <button onClick={() => onDelete(user.rut)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};
