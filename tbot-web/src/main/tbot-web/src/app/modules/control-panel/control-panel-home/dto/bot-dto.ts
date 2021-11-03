import {UrlDTO} from './url-dto';
import {DomandaDto} from '../../../common/components/form-input/dto/domanda-dto';

export interface BotDTO {

  codiceBot: string;
  urls: Array<UrlDTO>;
  codiciUrls: Array<string>;
  _toggle: boolean;
  caratteristiche: Array<DomandaDto>;
  _nome?: string;
  _descrizione?: string;

}
