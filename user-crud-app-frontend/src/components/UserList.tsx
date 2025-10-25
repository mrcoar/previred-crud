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
      <div className="table-container">
        <table className="users-table">
          <thead>
            <tr>
              <th>RUT</th>
              <th>Nombre</th>
              <th>Apellido</th>
              <th>Fecha de Nacimiento</th>
              <th>Calle</th>
              <th>Región</th>
              <th>Comuna</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.rut || index} className="user-item">
                <td>{user.rut}</td>
                <td>{user.nombre}</td>
                <td>{user.apellido}</td>
                <td>{user.fechaNacimiento}</td>
                <td>{user.calle}</td>
                <td>{getRegionName(user.region)}</td>
                <td>{getComunaName(user.region, user.comuna)}</td>
                <td>
                  <button onClick={() => onEdit(user)} className="edit-btn">Edit</button>
                  <button onClick={() => onDelete(user.rut)} className="delete-btn">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
