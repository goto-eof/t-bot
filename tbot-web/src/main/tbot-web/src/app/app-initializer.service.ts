import {Injectable} from '@angular/core';
import {observable, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Configuration} from './configuration';
import {shareReplay} from 'rxjs/operators';
import {BaseUrls} from './base-urls';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppInitializerService {

  private readonly CONFIG_URL = '/t-bot-manager/app-configuration';
  calculatedConfigUrl: string;
  private readonly BASE_URLS_URL = '/t-bot-manager/app-configuration/base-urls';
  calculatedUrlsUrl: string;
  private configuration: Observable<Configuration>;
  private baseUrls: Observable<BaseUrls>;

  config: any;


  constructor(private http: HttpClient) {

    if (environment.production === false) {
      this.calculatedConfigUrl = environment.baseUrl + this.CONFIG_URL;
      this.calculatedUrlsUrl = environment.baseUrl + this.BASE_URLS_URL;

      this.config = {
        urlConfig: environment.baseUrl + '/t-bot-manager/url-configuration',
        botConfig: environment.baseUrl + '/t-bot-manager/bot-configuration',
        logConfig: environment.baseUrl + '/t-bot-manager/log'
      };

    } else {
      this.calculatedConfigUrl = location.origin + this.CONFIG_URL;
      this.calculatedUrlsUrl = location.origin + this.BASE_URLS_URL;

      this.config = {
        urlConfig: location.origin + '/t-bot-manager/url-configuration',
        botConfig: location.origin + '/t-bot-manager/bot-configuration',
        logConfig: location.origin + '/t-bot-manager/log'
      };

    }

  }

  getConfiguration(): Observable<Configuration> {
    if (!this.configuration) {
      this.configuration = this.http.get<Configuration>(this.calculatedConfigUrl).pipe(
        shareReplay(1)
      );
    }
    return this.configuration;
  }

  getBaseUrls(): Observable<BaseUrls> {
    if (!this.baseUrls) {
      const obs = new Observable<BaseUrls>(observer => {
        observer.next(this.config);
        observer.complete();
      });
      return obs;
      /*      this.baseUrls = this.http.get<BaseUrls>(this.calculatedUrlsUrl).pipe(
              shareReplay(1)
            );*/
    }
    return null;
  }
}
