import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';
import {CreatePriceDto, PriceDto} from './price.model';

export default class PriceApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getPrices() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, PriceDto[]>('/api/prices', config);
  }

  getPrice(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, PriceDto>(`/api/prices/${id}`, config);
  }

  createPrice(priceDto: CreatePriceDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.post<CreatePriceDto, PriceDto>('/api/prices', priceDto, config);
  }

  updatePrice(id: number, priceDto: PriceDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<PriceDto, PriceDto>(`/api/prices/${id}`, priceDto, config);
  }

  deletePrice(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(`/api/prices/${id}`, config);
  }

}
