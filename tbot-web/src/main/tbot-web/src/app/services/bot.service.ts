import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {AppInitializerService} from '../app-initializer.service';
import {HttpClient} from '@angular/common/http';
import {BotDTO} from '../modules/control-panel/control-panel-home/dto/bot-dto';

@Injectable({
  providedIn: 'root'
})
export class BotService {

  constructor(private init: AppInitializerService,
              private http: HttpClient) {
  }

  scheduleBotConfigurationReload(bot: BotDTO, reload: boolean = true): Observable<boolean> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.post<boolean>(urls.botConfig + '/reloadConfiguration/codiceBot/' + bot.codiceBot + '/reload/' + reload, {});
    }));
  }



  scheduleAllConfigurationReload(bot: BotDTO, reload: boolean = true): Observable<boolean> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.post<boolean>(urls.botConfig + '/reloadAllConfiguration/codiceBot/' + bot.codiceBot + '/reload/' + reload, {});
    }));
  }
}
