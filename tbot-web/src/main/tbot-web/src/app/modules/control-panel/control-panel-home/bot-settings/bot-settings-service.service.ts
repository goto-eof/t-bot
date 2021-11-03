import {Injectable} from '@angular/core';
import {BotDTO} from '../dto/bot-dto';
import * as _ from 'underscore';
import {DomandaDto} from '../../../common/components/form-input/dto/domanda-dto';

@Injectable({
  providedIn: 'root'
})
export class BotSettingsServiceService {

  constructor() {
  }

  retrieveBotName(bot: BotDTO): string {
    // D030 - Bot name
    const domandaDto = this.retrievedomandaDto(bot, 'D030');
    return !domandaDto ? null : domandaDto.valore;
  }

  retrieveBotDescription(bot: BotDTO): string{
    // D040 - Bot description
    const domandaDto = this.retrievedomandaDto(bot, 'D040');
    return !domandaDto ? null : domandaDto.valore;
  }

  private retrievedomandaDto(bot: BotDTO, codiceDomanda: string): DomandaDto{
    return _.find(bot.caratteristiche, carat => {
      if (carat.id === codiceDomanda){
        return carat.valore;
      }
    });
  }
}
