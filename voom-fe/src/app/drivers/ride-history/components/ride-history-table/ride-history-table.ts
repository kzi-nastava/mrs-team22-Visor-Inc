import {
  AfterViewInit,
  Component,
  ViewChild,
  inject,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { MatSort, Sort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

// <!-- for tabel we need date, start time, end time, source, destination, status, price, open passenger list -->

type TimeString = `${number}:${number}`;

export interface Passenger {
  name: string;
  lastname: string;
  orderedRide: boolean;
}

export interface Ride {
  date: Date;
  startTime: TimeString;
  endTime: TimeString | '—';
  source: string;
  destination: string;
  status: string;
  price: number;
  panic: boolean;
  passengers: Passenger[];
}

const RIDE_DATA: Ride[] = [
  {
    date: new Date('2025-03-01'),
    startTime: '08:15',
    endTime: '08:45',
    source: 'Bulevar Oslobođenja 45',
    destination: 'Železnička stanica Novi Sad',
    status: 'Completed',
    price: 850,
    passengers: [
      { name: 'Marko', lastname: 'Petrović', orderedRide: true },
      { name: 'Ivan', lastname: 'Jovanović', orderedRide: false },
    ],
    panic: false,
  },
  {
    date: new Date('2025-03-01'),
    startTime: '10:30',
    endTime: '11:05',
    source: 'Limanski park',
    destination: 'Futoška pijaca',
    status: 'Completed',
    price: 720,
    passengers: [{ name: 'Ana', lastname: 'Nikolić', orderedRide: true }],
    panic: false,
  },
  {
    date: new Date('2025-03-02'),
    startTime: '09:00',
    endTime: '09:40',
    source: 'Detelinara',
    destination: 'FTN',
    status: 'Completed',
    price: 900,
    passengers: [
      { name: 'Stefan', lastname: 'Ilić', orderedRide: true },
      { name: 'Maja', lastname: 'Kovačević', orderedRide: false },
      { name: 'Luka', lastname: 'Marić', orderedRide: false },
    ],
    panic: false,
  },
  {
    date: new Date('2025-03-03'),
    startTime: '14:10',
    endTime: '—',
    source: 'Spens',
    destination: 'Centar',
    status: 'Cancelled by you',
    price: 0,
    passengers: [{ name: 'Jelena', lastname: 'Popović', orderedRide: true }],
    panic: false,
  },
  {
    date: new Date('2025-03-03'),
    startTime: '16:20',
    endTime: '16:55',
    source: 'Novo Naselje',
    destination: 'Promenada',
    status: 'Completed',
    price: 780,
    passengers: [
      { name: 'Nemanja', lastname: 'Stojanović', orderedRide: true },
      { name: 'Filip', lastname: 'Đorđević', orderedRide: false },
    ],
    panic: false,
  },
  {
    date: new Date('2025-03-04'),
    startTime: '07:40',
    endTime: '08:10',
    source: 'Veternik',
    destination: 'Centar',
    status: 'Completed',
    price: 950,
    passengers: [{ name: 'Milica', lastname: 'Ristić', orderedRide: true }],
    panic: false,
  },
  {
    date: new Date('2025-03-04'),
    startTime: '12:00',
    endTime: '12:35',
    source: 'Telep',
    destination: 'Klinički centar',
    status: 'Completed',
    price: 820,
    passengers: [
      { name: 'Ognjen', lastname: 'Savić', orderedRide: true },
      { name: 'Petar', lastname: 'Milošević', orderedRide: false },
    ],
    panic: true,
  },
  {
    date: new Date('2025-03-05'),
    startTime: '18:30',
    endTime: '19:05',
    source: 'Centar',
    destination: 'Petrovaradin',
    status: 'Completed',
    price: 880,
    passengers: [{ name: 'Sara', lastname: 'Lazić', orderedRide: true }],
    panic: false,
  },
  {
    date: new Date('2025-03-06'),
    startTime: '20:10',
    endTime: '—',
    source: 'Promenada',
    destination: 'Limanski park',
    status: `Cancelled by ${'Vuk'}`,
    price: 0,
    passengers: [{ name: 'Vuk', lastname: 'Obradović', orderedRide: true }],
    panic: false,
  },
  {
    date: new Date('2025-03-06'),
    startTime: '22:15',
    endTime: '22:50',
    source: 'Petrovaradin',
    destination: 'Novo Naselje',
    status: 'Completed',
    price: 920,
    passengers: [
      { name: 'Teodora', lastname: 'Pavlović', orderedRide: true },
      { name: 'Andrej', lastname: 'Mitrović', orderedRide: false },
    ],
    panic: false,
  },
];

@Component({
  selector: 'app-ride-history-table',
  imports: [MatTableModule, MatSortModule],
  templateUrl: './ride-history-table.html',
  styleUrl: './ride-history-table.css',
})

// <!-- for tabel we need date, start time, end time, source, destination, status, price, open passenger list
export class RideHistoryTable implements AfterViewInit {
  displayedColumnsRide: string[] = [
    'date',
    'startTime',
    'endTime',
    'source',
    'destination',
    'status',
    'price',
    'panic',
    'openPassengerList',
  ];

  @Input() startDate!: Date | null;
  @Input() endDate!: Date | null;

  expandedRow: Ride | null = null;

  // rideDataSource = new MatTableDataSource(RIDE_DATA);

  @ViewChild(MatSort) sort: MatSort = new MatSort();

  toggleRow(row: Ride) {
    this.expandedRow = this.expandedRow === row ? null : row;
  }

  rideDataSource = new MatTableDataSource(RIDE_DATA);

  ngOnChanges(changes: SimpleChanges) {
    if (changes['startDate'] || changes['endDate']) {
      this.applyFilter();
    }
  }

  applyFilter() {
    console.log(this.startDate);
    this.rideDataSource.data = RIDE_DATA.filter((ride) => {
      const rideDate = new Date(ride.date).getTime();
      const start = this.startDate ? new Date(this.startDate).getTime() : null;
      const end = this.endDate ? new Date(this.endDate).getTime() : null;

      if (start && rideDate < start) return false;
      if (end && rideDate > end) return false;

      return true;
    });
  }

  ngAfterViewInit() {
    this.rideDataSource.sort = this.sort;
    this.rideDataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'date':
          return item.date.getTime();
        default:
          return (item as any)[property];
      }
    };
  }
}
