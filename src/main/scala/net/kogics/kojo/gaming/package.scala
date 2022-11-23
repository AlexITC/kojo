/*
 * Copyright (C) 2022 Lalit Pant <pant.lalit@gmail.com>
 * Copyright (C) 2022 Anay Kamat <kamatanay@gmail.com>
 *
 * The contents of this file are subject to the GNU General Public License
 * Version 3 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.gnu.org/copyleft/gpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package net.kogics.kojo

import net.kogics.kojo.core.{Picture, Point, SCanvas}

package object gaming {
  trait GameMsgSink[Msg] {
    def triggerUpdate(msg: Msg): Unit
  }

  trait Sub[Msg] {
    def fire(gameMsgSink: GameMsgSink[Msg]): Unit
  }

  object Subscriptions {
    case class OnAnimationFrame[Msg](mapper: () => Msg) extends Sub[Msg] {
      def fire(gameMsgSink: GameMsgSink[Msg]): Unit = {
        val msg = mapper()
        gameMsgSink.triggerUpdate(msg)
      }
    }

    case class OnKeyDown[Msg](mapper: Int => Msg) extends Sub[Msg] {
      def fire(gameMsgSink: GameMsgSink[Msg]): Unit = {
        val pressedKeys = net.kogics.kojo.staging.Inputs.pressedKeys
        pressedKeys.foreach { keyCode =>
          val msg = mapper(keyCode)
          gameMsgSink.triggerUpdate(msg)
        }
      }
    }

    case class OnMousePress[Msg](mapper: Point => Msg) extends Sub[Msg] {
      def fire(gameMsgSink: GameMsgSink[Msg]): Unit = {
        import net.kogics.kojo.staging.Inputs
        if (Inputs.mousePressedFlag) {
          val msg = mapper(Inputs.mousePos)
          gameMsgSink.triggerUpdate(msg)
        }
      }
    }

    def onAnimationFrame[Msg](mapper: => Msg): Sub[Msg] = OnAnimationFrame(() => mapper)

    def onKeyDown[Msg](mapper: Int => Msg): Sub[Msg] = OnKeyDown(mapper)

    def onMousePress[Msg](mapper: Point => Msg): Sub[Msg] = OnMousePress(mapper)
  }

  class Game[Model, Msg](
                          init: => Model,
                          update: (Model, Msg) => Model,
                          view: Model => Picture,
                          subscriptions: Model => Seq[Sub[Msg]]
                        )(implicit canvas: SCanvas) extends GameMsgSink[Msg] {
    private var currModel: Model = _
    private var currSubs: Seq[Sub[Msg]] = _
    private var currView: Picture = _
    private var firstTime = true

    var gameTimer = canvas.timer(20) {
      if (firstTime) {
        firstTime = false
        currModel = init
        currView = view(currModel)
        currView.draw()
        currSubs = subscriptions(currModel)
      }
      else {
        fireTimerSubs()
      }
    }

    def fireTimerSubs(): Unit = {
      currSubs.foreach { sub =>
        sub.fire(this)
      }
      if (currSubs.isEmpty) {
        canvas.stopAnimationActivity(gameTimer)
      }
    }

    def triggerUpdate(msg: Msg): Unit = {
      if (currSubs.nonEmpty) {
        currModel = update(currModel, msg)
        val oldView = currView
        currView = view(currModel)
        oldView.erase()
        currView.draw()
        currSubs = subscriptions(currModel)
      }
    }
  }

  class CollisionDetector(implicit canvas: SCanvas) {
    val cb = canvas.cbounds
    val minX = cb.getMinX
    val minY = cb.getMinY
    val maxX = cb.getMaxX
    val maxY = cb.getMaxY

    def collidesWithHorizontalEdge(x: Double, w: Double): Boolean =
      !(x >= minX && x <= (maxX - w))

    def collidesWithVerticalEdge(y: Double, h: Double): Boolean =
      !(y >= minY && y <= (maxY - h))

    def collidesWithEdge(x: Double, y: Double, w: Double, h: Double): Boolean = {
      !((x >= minX && x <= (maxX - w)) && (y >= minY && y <= (maxY - h)))
    }

    def collidesWith(
                      x1: Double, y1: Double, w1: Double, h1: Double,
                      x2: Double, y2: Double, w2: Double, h2: Double
                    ): Boolean = {
      import java.awt.geom.Rectangle2D
      val r1 = new Rectangle2D.Double(x1, y1, w1, h1)
      val r2 = new Rectangle2D.Double(x2, y2, w2, h2)
      r1.intersects(r2)
    }
  }
}
