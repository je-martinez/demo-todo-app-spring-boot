import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { AppInitializerService } from './app/services';

bootstrapApplication(App, appConfig)
  .then(appRef => {
    // Initialize app after bootstrap
    const initializer = appRef.injector.get(AppInitializerService);
    return initializer.initializeApp();
  })
  .catch(err => console.error(err));
