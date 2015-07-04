# Tetris

Start empty world, with a random peice.
Peice drops every t.
User can spin and straffe the peice.
When peice lands (collides), full rows are removed.
New peice comes down when peice lands.
Stop when no space.

* svg
* keyboard input rotate l/r space
* matrix for screen/rules
* matrix (represeting the world)
* -> matrix (next-step world)
* our peice
* x, y, rotation, matrix
* score

    11
    11

    11
     11

block-pile:

    000000
    000000
    000000
    000011
    111111
    100110

    ->

    000000
    000011
    100110


## Development

    lein figwheel

Open your browser at [localhost:3449](http://localhost:3449/).

To create a production build run:

    lein cljsbuild once min

And open `resources/public/index.html`.


## License

Copyright © 2014 Timothy Pratley

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
