import {AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {BotDTO} from '../dto/bot-dto';
import {AppInitializerService} from '../../../../app-initializer.service';
import {BotSettingsServiceService} from './bot-settings-service.service';
import {HttpClient} from '@angular/common/http';
import {CommonService} from '../../../../services/common.service';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {BotService} from '../../../../services/bot.service';
import {FormBuilder, FormGroup, NgForm} from '@angular/forms';

@Component({
  selector: 'app-bot-settings',
  templateUrl: './bot-settings.component.html',
  styleUrls: ['./bot-settings.component.css']
})
export class BotSettingsComponent implements OnInit, AfterViewInit, AfterContentChecked {

  @Input()
  bot: BotDTO;

  myForm: FormGroup;

  botName: string;
  botDescription: string;

  constructor(private init: AppInitializerService,
              private botSettingsService: BotSettingsServiceService,
              private http: HttpClient,
              private commonService: CommonService,
              private botService: BotService,
              private fb: FormBuilder,
              private cdr: ChangeDetectorRef) {
    this.commonService.commonServiceEmitter.subscribe((data: any) => {
      if (data.data === 'update') {
        console.log('Daa: ', data);
        this.ricalcolaProprieta();
      }
    });
    this.myForm = this.fb.group({});
  }

  ngOnInit(): void {
    this.bot.caratteristiche.forEach(ele => {
      console.log(ele.id + ' - ' + ele.obbligatorio)
    });
    this.ricalcolaProprieta();
  }

  ngAfterViewInit(): void {

  }

  ngAfterContentChecked(): void {
    this.cdr.detectChanges();
  }

  salva(bot: BotDTO): Observable<any> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.put(urls.botConfig + '/codiceBot/' + bot.codiceBot, bot);
    }));
  }

  salvaEVaiAllaHomeDelBot(bot: BotDTO): void {
    if (!this.isFormInvalidCalculator()) {
      this.salva(bot).subscribe(response => {
        this.commonService.commonServiceEmitter.emit({command: 'go_to_tab_0'});
      });
    }
  }

  ricalcolaProprieta(): void {
    this.bot._nome = this.botSettingsService.retrieveBotName(this.bot);
    this.bot._descrizione = this.botSettingsService.retrieveBotDescription(this.bot);
  }


  salvaEAggiornaConfigurazioneEVaiAllaHomeDelBot(bot: BotDTO): void {
    if (!this.isFormInvalidCalculator()) {
      this.salva(bot).subscribe(data => {
        this.botService.scheduleBotConfigurationReload(bot).subscribe(result => {
          if (result === true) {
            this.commonService.commonServiceEmitter.emit({command: 'go_to_tab_0'});
            this.ricalcolaProprieta();
          }
        });
      });
    }
  }


  isFormInvalidCalculator(): boolean {
    return this.myForm.invalid;
  }
}
