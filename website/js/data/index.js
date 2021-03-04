import {Title} from "../builder/components/Title";
import {Dashboard, DashboardButton} from "../builder/components/Dashboard";

export async function getData() {
  return [
    new Title('Neolithic Game'),
    new Dashboard([
      new DashboardButton('/news.html', 'screen02.png', 'News'),
      new DashboardButton('/docs.html', 'screen03.png', 'Documen&shy;tation'),
      new DashboardButton('/media.html', 'screen04.png', 'Screen&shy;shots'),
    ])
  ];
}
