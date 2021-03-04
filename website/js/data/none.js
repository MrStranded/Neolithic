import {Title} from "../builder/components/Title";
import {Dashboard, DashboardButton} from "../builder/components/Dashboard";

export async function getData() {
  return[
    new Title('404 - Not Found'),
    new Dashboard([
      new DashboardButton('/', 'screen03.png', 'Back to Home Page')
    ])
  ];
}
