import {Component, Input, OnInit} from '@angular/core';
import {BotDTO} from '../dto/bot-dto';
import {AppInitializerService} from '../../../../app-initializer.service';
import {HttpClient} from '@angular/common/http';
import {CPBotWrapper} from '../../../../dto/cpbot-wrapper';
import {BotSettingsServiceService} from '../bot-settings/bot-settings-service.service';
import {CommonService} from '../../../../services/common.service';
import {DialogService} from '../../../../services/dialog.service';

@Component({
  selector: 'app-bot-info',
  templateUrl: './bot-info.component.html',
  styleUrls: ['./bot-info.component.css']
})
export class BotInfoComponent implements OnInit {

  @Input()
  botWrapper: CPBotWrapper;


  botName: string;

  constructor(private init: AppInitializerService,
              private http: HttpClient,
              private commonService: CommonService,
              private botSettingsService: BotSettingsServiceService,
              private dialogService: DialogService) {
  }


  ngOnInit(): void {
    this.botName = this.botSettingsService.retrieveBotName(this.botWrapper.selectedBot);
    this.commonService.commonServiceEmitter.subscribe(message => {
      if ('update' === message.data) {
        this.botName = this.botSettingsService.retrieveBotName(this.botWrapper.selectedBot);
      }
    });
  }

  removeBot(bot: BotDTO): void {
    this.dialogService.openYesNoDialog('Eliminazione bot', 'Sei sicuro di voler elimianre il bot?').afterClosed().subscribe(answer => {
      if (answer) {
        this.deleteBot(bot);
      }
    });
  }

  private deleteBot(bot: BotDTO): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.delete(urls.botConfig + '/codiceBot/' + bot.codiceBot).subscribe(response => {
        if (response === true) {
          console.log(response);
          this.botWrapper.bots.splice(this.botWrapper.bots.indexOf(bot), 1);
          console.log(this.botWrapper.bots);
          this.botWrapper.selectedBot = null;
        }
      });
    });
  }
}
