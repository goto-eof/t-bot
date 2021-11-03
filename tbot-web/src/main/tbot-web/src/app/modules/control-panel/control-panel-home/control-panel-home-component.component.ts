import {Component, EventEmitter, Inject, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UrlDTO} from './dto/url-dto';
import {BotDTO} from './dto/bot-dto';
import {AppInitializerService} from '../../../app-initializer.service';
import {HttpClient} from '@angular/common/http';
import {BotSettingsServiceService} from './bot-settings/bot-settings-service.service';
import * as _ from 'underscore';
import {BotSettingsComponent} from './bot-settings/bot-settings.component';
import {CommonService} from '../../../services/common.service';
import {CaratteristicheService} from '../../../services/caratteristiche.service';
import {CPBotWrapper} from '../../../dto/cpbot-wrapper';
import {BotService} from '../../../services/bot.service';
import {Router} from '@angular/router';
import {MenuStatusDTO} from '../../../dto/menu-status-dto';
import {MatSidenav} from '@angular/material/sidenav';
import {DialogService} from '../../../services/dialog.service';
import {environment} from '../../../../environments/environment';

interface Food {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-control-panel-home',
  templateUrl: './control-panel-home-component.component.html',
  styleUrls: ['./control-panel-home-component.component.css']
})

export class ControlPanelHomeComponent implements OnInit {
  @Input() data: BotSettingsComponent;

  selectedIndex = 0;
  @ViewChild('drawer')
  drawer: MatSidenav;

  menuStatusDTO: MenuStatusDTO;

  isProduction: boolean;

  botWrapper: CPBotWrapper;

  isMenuOpened = true;


  constructor(private init: AppInitializerService,
              private http: HttpClient,
              private botService: BotService,
              private botSettingService: BotSettingsServiceService,
              private commonService: CommonService,
              private dialogService: DialogService,
              private caratteristicheService: CaratteristicheService,
              private router: Router) {
    this.botWrapper = {
      selectedBot: null,
      bots: []
    };
    this.isProduction = environment.production;
    this.menuStatusDTO = {
      menuGroup: 0,
      selectedIndex: 0,
      selectedIndexUrl: 0
    };
    this.commonService.commonServiceEmitter.subscribe(data => {
      if (data.command === 'go_to_tab_0') {
        this.selectedIndex = 0;
      }
    });
  }

  ngOnInit(): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.get<Array<BotDTO>>(urls.botConfig).subscribe(bots => {
        this.botWrapper.bots = bots;
        _.forEach(this.botWrapper.bots, bot => this.retrieveBotUrls(bot));
        console.log(bots);
      });
    });
  }

  openPanelActions(bot: BotDTO): void {

  }

  closePanelActions(bot: BotDTO): void {
    bot._toggle = false;
  }

  edit(bot: BotDTO): void {
    this.selectedIndexChange(1);
    this.clickPanelActions(bot);
  }

  clickPanelActions(bot: BotDTO): void {
    this.menuStatusDTO.menuGroup = 1;
    this.botWrapper.selectedBot = bot;
    this.botWrapper.selectedBot._nome = this.botSettingService.retrieveBotName(bot);
    this.botWrapper.selectedBot._descrizione = this.botSettingService.retrieveBotDescription(bot);
    this.commonService.commonServiceEmitter.emit({
      data: 'update'
    });
    this.drawer.close();
  }

  retrieveBotName(bot: BotDTO): string {
    return this.botSettingService.retrieveBotName(bot);
  }

  retrieveBotDescription(bot: BotDTO): string {
    return this.botSettingService.retrieveBotDescription(bot);
  }

  isBotSelected(bot: BotDTO): boolean {
    return !!bot;
  }


  removeBot(bot: BotDTO): void {
    this.dialogService.openYesNoDialog('Eliminazione bot', 'Sei sicuro di voler elimianre il bot?').afterClosed().subscribe(answer => {
      if (answer) {
        this.deleteBot(bot);
      }
    });
  }

  deleteBot(bot: BotDTO): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.delete(urls.botConfig + '/codiceBot/' + bot.codiceBot).subscribe(response => {
        if (response === true) {
          this.botWrapper.bots.splice(this.botWrapper.bots.indexOf(bot), 1);
          this.botWrapper.selectedBot = null;
          this.drawer.close();
        }
      });
    });
  }

  newUrl(bot: BotDTO): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.post<UrlDTO>(urls.urlConfig + '/codiceBot/' + bot.codiceBot, {}).subscribe(response => {
        bot.urls.push(response);
        response._reloadAllConfiguration = true;
        response._name = response.codiceQuestionario;
        this.clickPanelActions(bot);
        this.selectedIndexChange(2);
        this.selectedIndexChangeUrl(bot.urls.length - 1);
        this.drawer.close();
      });
    });
  }

  newBot(): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.post<BotDTO>(urls.botConfig, {}).subscribe(data => {
        this.botWrapper.bots.push(data);
        this.clickPanelActions(data);
        this.drawer.close();
        this.selectedIndexChange(1);
        console.log(data);
      });
    });
  }

  retrieveBotUrls(bot: BotDTO): void {
    this.init.getBaseUrls().subscribe(urls => {
      this.http.get<Array<UrlDTO>>(urls.urlConfig + '/codiceBot/' + bot.codiceBot).subscribe(response => {
        bot.urls = response;
        _.forEach(bot.urls, (url: UrlDTO) => {
          const carUrlName = this.caratteristicheService.retrieveDomandaDto(url.caratteristiche, 'U000');
          if (carUrlName) {
            url._name = carUrlName.valore;
          } else {
            url._name = url.codiceQuestionario;
          }
        });
        console.log('-----URLS-----', response);
      });
    });
  }

  removeUrl(bot: BotDTO, url: UrlDTO): void {
    this.init.getBaseUrls().subscribe(urls => {
      const deleteUrl = urls.urlConfig + '/codiceBot/' + bot.codiceBot + '/codiceQuestionario/' + url.codiceQuestionario;
      this.http.delete<boolean>(deleteUrl).subscribe(response => {
        if (response === true) {
          bot.urls.splice(bot.urls.indexOf(url), 1);
          this.botService.scheduleAllConfigurationReload(bot, true).subscribe(result => {
            console.log('Richiesta reload di tutta la configurazione eseguita con successo');
          });
        }
      });
    });
  }

  selectedIndexChangeMainTabs(val: number): void {
    this.selectedIndexChange(val);
    if (val === 2) {
      this.selectedIndexChangeUrl(0);
    }
  }

  selectedIndexChange(val: number): void {
    this.selectedIndex = val;
    this.menuStatusDTO.selectedIndex = val;

  }

  goToLog(): void {
    this.menuStatusDTO.menuGroup = 0;
    this.botWrapper.selectedBot = null;
    this.drawer.close();
  }

  selectedIndexChangeUrl(val: number): void {
    this.menuStatusDTO.selectedIndexUrl = val;
  }
}
