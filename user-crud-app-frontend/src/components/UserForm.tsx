import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { format, parse } from 'date-fns';
import { es } from 'date-fns/locale/es';
import type { User, Region, Comuna, Address } from '../types';

interface UserFormProps {
  user?: User | null;
  onSubmit: (data: User) => Promise<{ success: boolean; message?: string }>;
  message?: string;
  setMessage?: (msg: string | null) => void;  // New: Clear message
  setMessageType: (msg: string | null) => void;  // New: Clear message
  onCancel?: () => void;
  regions: Region[];
  getComunasForRegion: (regionId: string) => Comuna[];
}

const API_BASE = 'http://localhost:8080/previred';

export const UserForm: React.FC<UserFormProps> = ({ user, onSubmit, onCancel, regions, getComunasForRegion }) => {
	interface FormData {
	  rut: string;
	  nombre: string;
	  apellido: string;
	  fechaNacimiento: string;
	  calle: string;
	  region: string;
	  comuna: number | undefined;
	}
  const [formData, setFormData] = useState<User>({
    rut: '',  // String now
    nombre: '',
    apellido: '',
    fechaNacimiento: '',
    calle: '',
    region: '',
    comuna: undefined,
  });
  const [currentComunas, setCurrentComunas] = useState<Comuna[]>([]);
  const [loadingRegion, setLoadingRegion] = useState(false);
  const [message, setMessage] = useState<string | null>(null);  // New: For API message
  const [messageType, setMessageType] = useState<'success' | 'error' | null>(null);  // New: Type for styling  

  // Helper to parse ISO string to Date for DatePicker
  const parseDate = (dateString: string): Date | null => {
    return dateString ? parse(dateString, 'yyyy-MM-dd', new Date()) : null;
  };

  // Helper to format Date to ISO string
  const formatDate = (date: Date | null): string => {
    return date ? format(date, 'yyyy-MM-dd') : '';
  };
  
  const fetchRegionFromComuna = async (comunaId: number) => {
    if (!comunaId) return;
    setLoadingRegion(true);
    try {
      const res = await fetch(`${API_BASE}/comuna/regionDeComuna/${comunaId}`);
      if (res.ok) {
        const regionIdText = await res.text();
        // Assume plain text is the region ID (string) or error message
        if (regionIdText && res.status == 200) {  // Simple check; adjust based on backend format
          setFormData(prev => ({ ...prev, region: regionIdText.trim() }));
          setCurrentComunas(getComunasForRegion(regionIdText.trim()));
		  setFormData(prev => ({ ...prev, comuna: comunaId }));
        } else {
          console.warn(`Error buscando region para la comuna ${comunaId}: ${regionIdText}`);
        }
      } else {
        console.error(`HTTP ${res.status} for regionDeComuna/${comunaId}`);
      }
    } catch (error) {
      console.error('Error buscando region de la comuna:', error);
    } finally {
      setLoadingRegion(false);
    }
  };
  
  useEffect(() => {
    if (user) {
		const ifn = (user.fechaNacimiento.indexOf('(')!=-1 ? 
				user.fechaNacimiento.substring(0, user.fechaNacimiento.indexOf('(')).trim() : user.fechaNacimiento);
		const afn = ifn.split("/");
		const ofn = afn[2] + "-" + afn[1] + "-" + afn[0];
		
    
      setFormData({
		rut: user.rut,
        nombre: user.nombre,
        apellido: user.apellido,
        fechaNacimiento: ofn,
        calle: user.calle,
        comuna: user.comuna,
      });
	  if (user.comuna) {
        fetchRegionFromComuna(user.comuna);  // Auto-fetch and set region
      } else {
        setCurrentComunas(getComunasForRegion(user.region || ''));
      }
    } else {
      setFormData({
		rut: '',
        nombre: '',
        apellido: '',
        fechaNacimiento: '',
        calle: '',
        region: '0',
        comuna: '0',
      });
      setCurrentComunas([]);
    }
  }, [user, getComunasForRegion]);
  
  useEffect(() => {
    if (message && setMessage) {
      const timer = setTimeout(() => {
        setMessage(null);  // Clear message
      }, 5000);  // 5 seconds
      return () => clearTimeout(timer);  // Cleanup on unmount or re-run
    }
  }, [message, setMessage]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    if (name.startsWith('address.')) {
      const field = name.split('.')[1] as keyof Address;
      setFormData(prev => ({
        ...prev,
        address: { ...prev.address, [field]: value },
      }));
    } else if (name === 'region') {
      setFormData(prev => ({ ...prev, [name]: value, comuna: '0' }));
      setCurrentComunas(getComunasForRegion(value));
    } else {
      setFormData(prev => ({ ...prev, [name]: name === 'comuna' ? Number(value) : value }));
    }
  };

  const handleDateChange = (date: Date | null) => {
    setFormData(prev => ({ ...prev, fechaNacimiento: formatDate(date) }));
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
	  e.preventDefault();
	  if (!formData.nombre || !formData.apellido || !formData.fechaNacimiento || !formData.calle || !formData.region || (user ? false : !formData.rut)) {
		setMessage('Por favor, llene los campos faltantes');
		setMessageType('error');
		return;
	  }

	  setMessage(null);
	  setMessageType(null);

	  try {
		const result = await onSubmit(formData);  // Await hook method
		console.log('handleSubmit result:', result);  // Debug
		if (result?.success) {
		  setMessage(result.message || 'Operación exitosa');
		  setMessageType('success');
		  // Reset with exact formData keys (Spanish)
		  setFormData({
			rut: '',
			nombre: '',
			apellido: '',
			fechaNacimiento: '',
			calle: '',
			region: '',  // '' for placeholder option
			comuna: undefined,  // Undefined for disabled select
		  });
		} else {
		  const errMsg = result?.message || 'Operación fallida';
		  setMessage(errMsg);
		  setMessageType('error');
		}
	  } catch (err) {
		const errMsg = 'Error inesperado. Chequee la consola del navegador';
		setMessage(errMsg);
		setMessageType('error');
		console.error('Submit error:', err);
	  }
	};
  const isEditing = !!user;
  const msgColor = messageType === 'error' ? 'red': 'green';
  const messageStyle = {
	  color: msgColor,  // e.g., color = 'red'
	  padding: '10px',
	  border: `1px solid ${msgColor}`,  // Template literal for concatenation
	  borderRadius: '4px',  // Optional: Nice touch
	  margin: '10px 0',
	  backgroundColor: msgColor === 'red' ? '#ffebee' : '#e8f5e8'  // Optional: Conditional background
	};
  return (
	<>
    {message && (
      <div className="error-message" style={messageStyle}>
        {message}
      </div>
    )}
    <form key={isEditing ? 'edit' : 'create'} onSubmit={handleSubmit} className="user-form">
      <h3>{isEditing ? `Editar Usuario con rut ${user?.rut}` : 'Agregar Usuario'}</h3>
      {!isEditing && (
        <input
          type="text"
          name="rut"
          value={formData.rut}
          onChange={handleChange}
          placeholder="RUT (e.g., 12345678-K)"
          required
          pattern="^\d{1,8}-[0-9K]$"  // 1-8 digits, dash, digit or K
          title="Formato: 1-8 dígitos con guion y dígito verificador"
          maxLength={11}  // 8 digits + - + 1 char = 10 max, buffer for validation
        />
      )}
      {isEditing && (
        <input type="hidden" name="rut" value={formData.rut} />
      )}
      <input name="nombre" value={formData.nombre} onChange={handleChange} placeholder="Nombre" required />
      <input name="apellido" value={formData.apellido} onChange={handleChange} placeholder="Apellido" required />
      <DatePicker
        selected={parseDate(formData.fechaNacimiento)}
        onChange={handleDateChange}
        dateFormat="yyyy-MM-dd"
        locale={es}
        placeholderText="Seleccione la fecha de nacimiento (DD/MM/YYYY)"
        required
        showMonthDropdown
        showYearDropdown
        dropdownMode="select"
        maxDate={new Date()}  // Prevent future dates
      />
      <input name="calle" value={formData.calle} onChange={handleChange} placeholder="Calle" required />
      <select name="region" value={formData.region} onChange={handleChange} required>
        <option value="0">Seleccione la región</option>
        {regions.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
      </select>
      <select name="comuna" value={formData.comuna?.toString() || ''} onChange={handleChange} disabled={!formData.region}>
        <option value="0">Seleccione la comuna asociada a la región</option>
        {currentComunas.map(c => <option key={c.id} value={c.id.toString()}>{c.nombre}</option>)}
      </select>
	  <div className="button-group">
		  <button type="submit">{user ? 'Actualizar' : 'Crear'}</button>
		  {onCancel && <button type="button" onClick={onCancel}>Cancelar</button>}
	  </div>
    </form>
	</>
  );
};