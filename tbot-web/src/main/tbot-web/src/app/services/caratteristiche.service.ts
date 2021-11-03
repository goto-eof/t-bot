import { Injectable } from '@angular/core';
import {BotDTO} from '../modules/control-panel/control-panel-home/dto/bot-dto';
import {DomandaDto} from '../modules/common/components/form-input/dto/domanda-dto';
import * as _ from 'underscore';

@Injectable({
  providedIn: 'root'
})
export class CaratteristicheService {

  constructor() { }


  public retrieveDomandaDto(caratteristiche: Array<DomandaDto>, codiceDomanda: string): DomandaDto{
    return _.find(caratteristiche, carat => {
      if (carat.id === codiceDomanda){
        return carat.valore;
      }
    });
  }
}
