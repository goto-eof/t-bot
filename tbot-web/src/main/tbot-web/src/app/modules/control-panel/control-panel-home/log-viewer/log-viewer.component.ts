import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {BotDTO} from '../dto/bot-dto';
import {BotSettingsServiceService} from '../bot-settings/bot-settings-service.service';
import {CommonService} from '../../../../services/common.service';
import {interval, Subscription} from 'rxjs';
import {LogService} from '../../../../services/log.service';
import {LogRowDTO} from '../../../../dto/log-row-dto';
import {CPBotWrapper} from '../../../../dto/cpbot-wrapper';
import {MenuStatusDTO} from '../../../../dto/menu-status-dto';

@Component({
  selector: 'app-log-viewer',
  templateUrl: './log-viewer.component.html',
  styleUrls: ['./log-viewer.component.css']
})
export class LogViewerComponent implements OnInit {

  readonly BUFFER_SIZE: number = 800;
  readonly CHECK_INTERVAL: number = 2000;
  readonly MAX_RETRY: number = 100;

  @Input()
  menuStatusDTO: MenuStatusDTO;

  @Input()
  botWrapper: CPBotWrapper;

  botName: string;

  logDownloader: Subscription;

  @ViewChild('element')
  root: ElementRef;

  logs: Array<LogRowDTO> = [];
  logsView: Array<LogRowDTO> = [];
  proceed = true;
  retryCount = 0;
  bot: BotDTO;

  constructor(private botSettingsService: BotSettingsServiceService,
              private logService: LogService,
              private commonService: CommonService) {
  }


  ngOnInit(): void {
    if (this.menuStatusDTO.menuGroup === 1) {
      this.bot = this.botWrapper.selectedBot;
      this.botName = this.botSettingsService.retrieveBotName(this.bot);
      this.commonService.commonServiceEmitter.subscribe(message => {
        if ('update' === message.data) {
          this.botName = this.botSettingsService.retrieveBotName(this.bot);
        }
      });
    }

    const source = interval(this.CHECK_INTERVAL);
    this.logDownloader = source.subscribe(val => this.downloadLog());
  }

  private downloadLog(): void {
    if (this.menuStatusDTO.menuGroup === 1 && this.botWrapper && this.menuStatusDTO.selectedIndex !== 3) {
      this.proceed = false;
      this.logDownloader.unsubscribe();
    }
    if (this.proceed === true) {
      this.proceed = false;
      if (this.menuStatusDTO.menuGroup === 1) {
        this.retrieveBotRows();
      } else {
        this.retrieveAllRows();
      }
    }
  }


  private retrieveBotRows(): void {
    if (this.bot) {
      this.logService.retrieveLogs(this.bot.codiceBot).subscribe(logs => {
        this.logs = this.logs.concat(logs);
        if (this.logs.length > this.BUFFER_SIZE) {
          this.logs = this.logs.splice(0, this.logs.length - this.BUFFER_SIZE);
        }
        this.logsView = Object.assign([], this.logs).reverse();
        this.proceed = true;
        this.retryCount = 0;
      }, error => {
        this.proceed = true;
        this.retryCount++;
        if (this.retryCount > this.MAX_RETRY) {
          this.proceed = false;
        }
      });
    }
  }


  private retrieveAllRows(): void {
    this.logService.retrieveAllLogs().subscribe(logs => {
      this.logs = this.logs.concat(logs);
      if (this.logs.length > this.BUFFER_SIZE) {
        this.logs = this.logs.splice(0, this.logs.length - this.BUFFER_SIZE);
      }
      this.logsView = Object.assign([], this.logs).reverse();
      this.proceed = true;
      this.retryCount = 0;
    }, error => {
      this.proceed = true;
      this.retryCount++;
      if (this.retryCount > this.MAX_RETRY) {
        this.proceed = false;
      }
    });
  }
}
