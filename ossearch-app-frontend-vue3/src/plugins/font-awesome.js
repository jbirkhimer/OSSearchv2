import { dom } from "@fortawesome/fontawesome-svg-core";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { fas } from "@fortawesome/free-solid-svg-icons"; //import all solid icons
import { fab } from "@fortawesome/free-brands-svg-icons"; //import all brand icons
// import { faUser } from "@fortawesome/free-solid-svg-icons" //import only the icons we are using
// import {
//   faHome,
//   faUser,
//   faUserPlus,
//   faSignInAlt,
//   faSignOutAlt,
// } from "@fortawesome/free-solid-svg-icons";

// library.add(faHome, faUser, faUserPlus, faSignInAlt, faSignOutAlt);

library.add(fas); //import all solid icons
library.add(fab); //import all brand icons
dom.watch(); // This will kick of the initial replacement of i to svg tags and configure a MutationObserver

export { FontAwesomeIcon };
