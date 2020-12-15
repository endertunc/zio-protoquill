package miniquill.parser

import scala.quoted._

object Mac {
  inline def enter(inline str: String): Unit = ${ enterImpl('str) }
  def enterImpl(str: Expr[String])(using qctx: Quotes): Expr[Unit] = {
    import qctx.reflect._
    println(pprint(Term.of(str).underlyingArgument))
    '{ () }
  }

  inline def passThrough(inline str: String): String = ${ passThroughImpl('str) }
  def passThroughImpl(str: Expr[String])(using qctx: Quotes): Expr[String] = {
    import qctx.reflect._
    class Operations(implicit val qctx: Quotes) extends TastyMatchers {
      //printExpr(str)
    }
    new Operations()
    str
  }

  
}

object MatchMac {
    import miniquill.quoter.QueryDsl._
    inline def apply(inline any: Any): Unit = ${ printMacImpl('any) }

    def printMacImpl(anyRaw: Expr[Any])(using Quotes): Expr[Unit] = {
      class Operations(implicit val qctx: Quotes) extends TastyMatchers {
        import quotes.reflect.{Ident => TIdent, Constant => TConstant, _}
        val any = Term.of(anyRaw).underlyingArgument.asExpr

        UntypeExpr(any) match {
          case Unseal(Block(parts, lastPart)) =>
            parts(0)
            
            parts.map(p => println("==== Matched! Exprs: " + Printer.TreeStructure.show(p) + " ===="))

            println(s"============= Matched! Expr: ${Printer.TreeStructure.show(lastPart)} =============")
          case other =>
            println(s"=============== Not Matched! =============")
            println(Printer.TreeStructure.show(Term.of(other)))

            println("================= Pretty Tree =================")
            println(pprint.apply(Term.of(other)))
        }
      }
      '{ () }
    }
}

object PrintMac {
    inline def apply(inline any: Any): Unit = ${ printMacImpl('any) }
    def printMacImpl(anyRaw: Expr[Any])(using Quotes): Expr[Unit] = {
      import quotes.reflect._
      val any = Term.of(anyRaw).underlyingArgument.asExpr
      class Operations(implicit val qctx: Quotes) extends TastyMatchers {
        import quotes.reflect._
        println("================= Tree =================")
        println(any.show)

        println("================= Matchers =================")
        println(Printer.TreeStructure.show(Untype(Term.of(any))))

        println("================= Pretty Tree =================")
        println(pprint.apply(Untype(Term.of(any))))
      }

      new Operations()
      '{ () }
    }
}
