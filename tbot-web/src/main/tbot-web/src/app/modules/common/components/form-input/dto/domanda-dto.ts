import {DominioDto} from './dominio-dto';

export interface DomandaDto {

  id: string;
  codiceGruppoDomanda: string;
  formato: string;
  testo: string;
  dominio: Array<DominioDto>;
  valore: string;
  suggerimento: string;
  obbligatorio: boolean;

}
