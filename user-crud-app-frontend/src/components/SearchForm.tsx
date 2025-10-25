// src/components/SearchForm.tsx (New Component)
import React, { useState } from 'react';
import type { Region, Comuna } from '../types';

interface SearchFormProps {
  regions: Region[];
  getComunasForRegion: (regionId: string) => Comuna[];
  onSearch: (params: { name?: string; lastname?: string; id?: string; street?: string; region?: string; comuna?: number; criteria: string }) => void;
}

export const SearchForm: React.FC<SearchFormProps> = ({ regions, getComunasForRegion, onSearch }) => {
  const [searchData, setSearchData] = useState({
    nombre: '',
    apellido: '',
    rut: '',
    calle: '',
    region: '',
    comuna: undefined as number | undefined,
    criteria: 'TODOS' as const,
  });
  const [currentComunas, setCurrentComunas] = useState<Comuna[]>([]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    if (name === 'regionId') {
      setSearchData(prev => ({ ...prev, [name]: value, comunaId: undefined }));
      setCurrentComunas(getComunasForRegion(value));
    } else {
      setSearchData(prev => ({ ...prev, [name]: name === 'comunaId' ? Number(value) : value }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch({
      nombre: searchData.nombre || undefined,
      apellido: searchData.apellido || undefined,
      rut: searchData.rut || undefined,
      calle: searchData.calle || undefined,
      region: searchData.region || undefined,
      comuna: searchData.comuna || undefined,
      criteria: searchData.criteria,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="user-form">
      <h3>Buscar Usuarios</h3>
      <select name="criteria" value={searchData.criteria} onChange={handleChange}>
        <option value="TODOS">Sin restricción</option>
        <option value="POR_NOMBRE">Por Nombre</option>
        <option value="POR_APELLIDO">Por Apellido</option>
        <option value="POR_NOMBRE_COMPLETO">Por Nombre y Apellido</option>
        <option value="POR_COMUNA">Por Comuna</option>
        <option value="POR_REGION">Por Region</option>
        <option value="POR_RUT">Por RUT</option>
      </select>
      <input name="nombre" value={searchData.name} onChange={handleChange} placeholder="Nombre" />
      <input name="apellido" value={searchData.lastname} onChange={handleChange} placeholder="Apellido" />
      <input name="rut" value={searchData.id} onChange={handleChange} placeholder="RUT (sin puntos, con guion y digito verificador)" />
      <input name="calle" value={searchData.street} onChange={handleChange} placeholder="Calle" />
      <select name="region" value={searchData.regionId} onChange={handleChange}>
        <option value="0">Seleccione una región</option>
        {regions.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
      </select>
      <select name="comuna" value={searchData.comunaId?.toString() || ''} onChange={handleChange} disabled={!searchData.regionId}>
        <option value="0">Seleccione una comuna</option>
        {currentComunas.map(c => <option key={c.id} value={c.id.toString()}>{c.nombre}</option>)}
      </select>
	  <div className="button-group">
		  <button type="submit">Buscar</button>
		  <button type="button" onClick={() => onSearch({ criteria: 'TODOS' })}>Limpiar</button>
	  </div>
    </form>
  );
};