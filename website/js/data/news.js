import {Title} from "../builder/components/Title";
import {NewsContainer, NewsItem, NewsItemPart} from "../builder/components/News";

export async function getData() {
  return [
    new Title('Neolithic News'),
    new NewsContainer([
      new NewsItem('First Blog Entry', '28th of February 2021', [
        new NewsItemPart(`
          <p>Welcome to <a href="https://neolithicgame.com">neolithicgame.com</a>!</p>
          <p>This website has been created to be a Hub for all things Neolithic-related.</p>
          <p>A big part of that is the <a href="/docs.html">NeoScript Documentation</a>, where you will find information about the custom script language that is used in the game.</p>
          <p>If you are tech savy, you might want to check out the <a href="https://github.com/MrStranded/Neolithic">GitHub page of the game</a>.</p>
        `),
        new NewsItemPart(`
          <h3>But what is Neolithic?</h3>
          <p>Neolithic is striving to be a stone age simulation, where you will try to influence a tribe and help them survive.</p>
          <p>Sounds vague? That's because it is. The game is in an early state of development and will be for some time. However, it is being worked on and little by litte, progress is made.</p>
        `),
        new NewsItemPart(`
          <h3>Curios?</h3>
          <p>All I've got so far are some <a href="/media.html">Screenshots</a>. Enjoy!</p>
        `)
      ])
    ])
  ];
}
