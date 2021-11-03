import {Component, Input, OnInit} from '@angular/core';
import {BotDTO} from '../dto/bot-dto';
import {AppInitializerService} from '../../../../app-initializer.service';
import {BotSettingsServiceService} from '../bot-settings/bot-settings-service.service';
import {HttpClient} from '@angular/common/http';
import {CommonService} from '../../../../services/common.service';
import {UrlDTO} from '../dto/url-dto';
import {CaratteristicheService} from '../../../../services/caratteristiche.service';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {UrlService} from '../../../../services/url.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {BotService} from '../../../../services/bot.service';

@Component({
  selector: 'app-url-settings',
  templateUrl: './url-settings.component.html',
  styleUrls: ['./url-settings.component.css']
})
export class UrlSettingsComponent implements OnInit {
  @Input()
  url: UrlDTO;

  @Input()
  selectedBot: BotDTO;

  urlName: string;

  myForm: FormGroup;


  constructor(private init: AppInitializerService,
              private botSettingsService: BotSettingsServiceService,
              private http: HttpClient,
              private botService: BotService,
              private commonService: CommonService,
              private caratteristicheService: CaratteristicheService,
              private urlService: UrlService,
              private fb: FormBuilder) {
    this.commonService.commonServiceEmitter.subscribe((data: any) => {
      if (data.data === 'url_refresh') {
        this.ricalcolaProprieta();
      }
    });
    this.myForm = this.fb.group({});
  }

  ngOnInit(): void {
    this.ricalcolaProprieta();
  }

  salva(url: UrlDTO): Observable<any> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.put(urls.urlConfig + '/codiceBot/' + this.selectedBot.codiceBot, url);
    }));
  }

  ricalcolaProprieta(): void {
    const carName = this.caratteristicheService.retrieveDomandaDto(this.url.caratteristiche, 'U000');
    if (carName) {
      this.urlName = carName.valore;
      this.url._name = carName.valore;
    }
  }

  salvaEVaiAllaHomeDelBot(url: UrlDTO): void {
    if (!this.isFormInvalidCalculator()) {
      this.salva(url).subscribe(data => {
        this.ricalcolaProprieta();
        this.commonService.commonServiceEmitter.emit({command: 'go_to_tab_0'});
      });
    }
  }

  salvaEAggiornaConfigurazioneEVaiAllaHomeDelBot(url: UrlDTO): void {
    if (!this.isFormInvalidCalculator()) {
      this.salva(url).subscribe(data => {
        if (url._reloadAllConfiguration === true) {
          this.botService.scheduleAllConfigurationReload(this.selectedBot, true).subscribe(result => {
            this.ricalcolaProprieta();
            this.commonService.commonServiceEmitter.emit({command: 'go_to_tab_0'});
          });
          url._reloadAllConfiguration = false;
        } else {
          this.urlService.scheduleUrlConfigurationReload(this.selectedBot.codiceBot, url, true).subscribe(result => {
            this.ricalcolaProprieta();
            this.commonService.commonServiceEmitter.emit({command: 'go_to_tab_0'});
          });
        }
      });
    }
  }

  isFormInvalidCalculator(): boolean {
    return this.myForm.invalid;
  }
}
