import {DomandaDto} from '../../../common/components/form-input/dto/domanda-dto';

export interface UrlDTO {
  codiceQuestionario: string;
  _name: string;
  caratteristiche: Array<DomandaDto>;
  _reloadAllConfiguration?: boolean;
}
