/* Interfaz usada para el <select> de las regiones */
export interface Region {
  id: string;
  nombre: string;
}

/* Interfaz usada para el <select> de las comunas */
export interface Comuna {
  id: number;
  nombre: string;
}

/* Interfaz usada para los usuarios */
export interface User {
  rut: string;
  nombre: string;
  apellido: string;
  fechaNacimiento: string; // ISO 'YYYY-MM-DD'
  calle: string;
  comuna: number;
}

export interface SearchParams {
  nombre?: string;
  apellido?: string;
  rut?: string;
  calle?: string;
  region?: string;
  comuna?: number;
  criterio: string;  // "All", "ByNombre", etc.
}