<?php
/**
 * This file is part of GameQ.
 *
 * GameQ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * GameQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

namespace GameQ\Protocols;

/**
 * Class Conanexiles
 *
 * @package GameQ\Protocols
 * @author Austin Bischoff <austin@codebeard.com>
 */
class Conanexiles extends Source
{
    /**
     * String name of this protocol class
     *
     * @type string
     */
    protected $name = 'conanexiles';

    /**
     * Longer string name of this protocol class
     *
     * @type string
     */
    protected $name_long = "Conan Exiles";

    /**
     * query_port = client_port + 19238
     *
     * @type int
     */
     protected $port_diff = 19238;
}
