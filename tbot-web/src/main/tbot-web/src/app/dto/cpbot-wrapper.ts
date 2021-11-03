import {BotDTO} from '../modules/control-panel/control-panel-home/dto/bot-dto';

export interface CPBotWrapper {
  selectedBot: BotDTO;
  bots: Array<BotDTO>;
  // selectedIndex: number;
}
