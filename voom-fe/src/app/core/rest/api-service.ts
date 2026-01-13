import {Injectable} from '@angular/core';
import {ApiClient} from './api-client';

@Injectable({
  providedIn: 'root',
})
export class ApiService {

  constructor(private apiClient: ApiClient) {}
}
