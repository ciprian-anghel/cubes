import { TestBed } from '@angular/core/testing';

import { OptionParserService } from './option-parser.service';

describe('OptionParserService', () => {
  let service: OptionParserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OptionParserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
