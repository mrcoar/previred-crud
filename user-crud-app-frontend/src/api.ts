import type { User, Region, Comuna, SearchParams } from './types';

const API_BASE = 'http://localhost:8080/previred';
const QUEUE_KEY = 'pending_user_actions';

interface PendingAction {
  type: 'create' | 'update' | 'delete';
  payload?: User | Partial<User> | Omit<User, "rut">;
  id?: string;
  timestamp: number;
}

const getQueue = (): PendingAction[] => {
  const stored = localStorage.getItem(QUEUE_KEY);
  return stored ? JSON.parse(stored) : [];
};

const setQueue = (queue: PendingAction[]) => 
	localStorage.setItem(QUEUE_KEY, JSON.stringify(queue));

const addToQueue = (action: PendingAction) => {
  const queue = getQueue();
  queue.push(action);
  setQueue(queue);
};

const processQueueItem = async (action: PendingAction): Promise<boolean> => {
  const headers = { 'Content-Type': 'application/json' };
  try {
    if (action.type === 'create') {
      const res = await fetch(`${API_BASE}/user/`, { method: 'PUT', headers, body: JSON.stringify(action.payload) });
      return res.ok;
    } else if (action.type === 'update') {
      const res = await fetch(`${API_BASE}/user/${action.id}`, { method: 'PUT', headers, body: JSON.stringify(action.payload) });
      return res.ok;
    } else if (action.type === 'delete') {
      const res = await fetch(`${API_BASE}/user/${action.id}`, { method: 'DELETE' });
      return res.ok;
    }
    return false;
  } catch {
    return false;
  }
};

const processQueue = async () => {
  const queue = getQueue();
  let i = 0;
  while (i < queue.length) {
    const success = await processQueueItem(queue[i]);
    if (success) queue.splice(i, 1);
    else i++;
  }
  setQueue(queue);
};

const syncIfOnline = () => navigator.onLine && processQueue();

window.addEventListener('online', syncIfOnline);

// Geo API
export const getRegions = async (): Promise<Region[]> => {
  try {
    const res = await fetch(`${API_BASE}/region/`);
    if (!res.ok) throw new Error();
    return await res.json();
  } catch {
    console.error('Failed to fetch regions');
    return [];
  }
};

export const getComunas = async (regionId: string): Promise<Comuna[]> => {
  try {
	if(regionId === '0') return [];
    const res = await fetch(`${API_BASE}/comuna/porRegion/${regionId}`);
    if (res.status != 200 && res.status!=204) throw new Error();
    const data = await res.json();
    return Array.isArray(data) ? data : []; // Empty array if not array
  } catch {
    console.error(`Failed to fetch comunas for ${regionId}`);
    return [];
  }
};

// User API
export const getUsers = async (params: SearchParams): Promise<User[] | string> => {
  if (!navigator.onLine) return [];
  try {
    const res = await fetch(`${API_BASE}/user/search`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params)
	});
	if(!res.ok && res.status >= 500) return "Error interno en el servidor";
	else if(!res.ok){
		console.warn(`Error ${res.status} detectado`);
		const text = await res.text();
		return text;
	}
    return await res.json();
  } catch {
    return [];
  }
};

export const createUser = async (user: User): Promise<{ success: boolean; data?: User; error?: string; message?: string }> => {
  if (navigator.onLine) {
    try {
      const res = await fetch(`${API_BASE}/user/`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      });
      if (!res.ok) {
        if (res.status >= 400 && res.status < 500) {
          const errorText = await res.text();
          return { success: false, error: errorText, message: errorText };
        }
        throw new Error('Server error');
      }
      const data = await res.json() as User & { message?: string };  // Assume backend adds message
      return { success: true, data, message: 'Usuario creado exitosamente' };
    } catch (error) {
      addToQueue({ type: 'create', payload: user, timestamp: Date.now() });
      return { success: true, data: user, message: 'Agregado a la cola para ser guardado posteriormente' };
    }
  } else {
    addToQueue({ type: 'create', payload: user, timestamp: Date.now() });
    return { success: true, data: user, message: 'Agregado a la cola para ser guardado posteriormente' };
  }
};

export const updateUser = async (id: string, user: Omit<User, "rut">): Promise<{ success: boolean; message: string } | null> => {
  var textMessage = "";
  var successFlag = false;
  if (navigator.onLine) {
    try {
      const res = await fetch(`${API_BASE}/user/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user),
      });
      if(!res.ok && res.status >= 500) throw new Error("Error interno en el servidor");
	  else if(!res.ok) console.warn(`Error ${res.status} detectado`);
	  successFlag = res.ok;
	  textMessage = await res.text();
    } catch (error) {
      addToQueue({ type: 'update', payload: user, id, timestamp: Date.now() });
      successFlag = true;
	  textMessage = "El usuario será actualizado cuando la conexión con la base de datos esté lista"; 
    }
  } else {
    addToQueue({ type: 'update', payload: user, id, timestamp: Date.now() });
    successFlag = true;
	textMessage = "El usuario será actualizado cuando el navegador esté en línea";
  }
  return {success: successFlag, message: textMessage};
};

export const deleteUser = async (id: string): Promise<{ success: boolean; message: string }> => {
  var textMessage = "";
  var successFlag = false;
  if (navigator.onLine) {
    try {
      const res = await fetch(`${API_BASE}/user/${id}`, { method: 'DELETE' });
	  console.log(res);
      if(!res.ok && res.status >= 500) throw new Error("Error interno en el servidor");
	  else if(!res.ok) console.warn(`Error ${res.status} detectado`);
	  successFlag = res.ok;
	  textMessage = await res.text();
    } catch (error) {
      addToQueue({ type: 'delete', id, timestamp: Date.now() });
      successFlag = true;
	  textMessage = "El usuario será eliminado cuando la conexión con la base de datos esté lista";
    }
  } else {
    addToQueue({ type: 'delete', id, timestamp: Date.now() });
    successFlag = true;
	textMessage = "El usuario será eliminado cuando el navegador esté en línea";
  }
  return {success: successFlag, message: textMessage};
};

syncIfOnline();