import { useState, useEffect } from 'react';
import { getRegions, getComunas } from '../api';
import type { Region, Comuna } from '../types';

export const useGeoData = () => {
  const [regions, setRegions] = useState<Region[]>([]);
  const [regionToComunas, setRegionToComunas] = useState<Map<string, Comuna[]>>(new Map());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadGeoData = async () => {
      setLoading(true);
      const regs = await getRegions();
      setRegions(regs);

      const allComunas = new Map<string, Comuna[]>();
      for (const reg of regs) {
        const comms = await getComunas(reg.id);
        allComunas.set(reg.id, comms);
      }
      setRegionToComunas(allComunas);
      setLoading(false);
    };

    loadGeoData();
  }, []);

  const getComunasForRegion = (regionId: string): Comuna[] => regionToComunas.get(regionId) || [];

  return { regions, regionToComunas, getComunasForRegion, loading };
};