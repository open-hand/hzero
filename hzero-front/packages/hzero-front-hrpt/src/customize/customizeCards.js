import { setCard } from 'hzero-front/lib/customize/cards';

setCard({
  code: 'ReportCard',
  component: async () => import('../routes/Cards/ReportCard'),
});
